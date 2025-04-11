package com.singularityuniverse.webpage.core

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.zIndex
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.sqrt

class WindowManager {
    var playGroundSize: IntSize? = null
    var screenDensity: Float? = null
    val windows = mutableStateListOf<Window>()
    val windowPosition = mutableStateMapOf<Window, IntOffset>()
    val windowOrder = mutableStateListOf<Window>()
    val shaker = mutableStateListOf<Window>()

    suspend fun open(window: Window): Boolean {
        if (windowOrder.contains(window)) {
            return bringToFront(window, true)
        }

        if (playGroundSize == null || screenDensity == null) {
            delay(300)
            return open(window)
        }

        val center = playGroundSize!!.center.minus(
            window.expectedSize.let {
                IntOffset(
                    x = (it.width.value * (screenDensity ?: 1f) / 2).toInt(),
                    y = (it.height.value * (screenDensity ?: 1f) / 2).toInt(),
                )
            }
        )
        windowPosition[window] = center
        windowOrder.add(window)
        return true
    }

    private var bringToFrontJob: Deferred<Unit>? = null
    suspend fun bringToFront(window: Window, animated: Boolean = false): Boolean = coroutineScope {
        val isOnTheFKNGTop = windowOrder.lastOrNull() == window
        if (isOnTheFKNGTop) {
            shake(window)
            return@coroutineScope true
        }

        if (!animated) {
            windowOrder.remove(window)
            windowOrder.add(window)
            return@coroutineScope true
        }

        // region sliding gimmick
        bringToFrontJob?.await() // ensure last animation is finished

        bringToFrontJob = async {
            val otherWindowsOnTopOfIt = run {
                val currentWindowPos = windowOrder.indexOf(window)
                val windowsOnTop = windowOrder.subList(currentWindowPos + 1, windowOrder.size)
                windowsOnTop
            }

            val currentWindowStart = windowPosition[window]?.x ?: return@async run {
                println("ERROR: window is not registered in windowPosition")
                false
            }
            val currentWindowCenter = currentWindowStart + window.center.x
            val currentWindowEnd = currentWindowStart + window.currentSize.value.width
            val currentWindowOccupation = currentWindowStart..currentWindowEnd

            // animate expose target window
            val delayBackToPosition = mutableMapOf<Window, IntOffset>()
            otherWindowsOnTopOfIt.forEach { subordinate ->
                val subOrdinateStart = windowPosition[subordinate]?.x ?: 0
                val subOrdinateEnd = subOrdinateStart + subordinate.currentSize.value.width
                val subOrdinateCenter = subOrdinateStart + subordinate.center.x

                // -1 (left); 0 (perfect center); 1 (right); null (not overlapping)
                val overlapDirection = run {
                    val subordinateOccupation = subOrdinateStart..subOrdinateEnd

                    // ignore if subordinate occupation not overlapping currentWindowOccupation
                    if (!subordinateOccupation.any { it in currentWindowOccupation }) return@run null

                    (subOrdinateCenter - currentWindowCenter).let { it / it.absoluteValue }
                } ?: return@forEach // ignore if not overlapping

                val overlapMagnitude = when (overlapDirection) {
                    -1 -> {
                        currentWindowStart - subOrdinateEnd - ((screenDensity?.absoluteValue
                            ?: 1f) * 16)// extra 16 dp
                    }

                    else -> {
                        currentWindowEnd - subOrdinateStart + ((screenDensity?.absoluteValue
                            ?: 1f) * 16)// extra 16 dp
                    }
                }.toInt()

                val targetPos = IntOffset(overlapMagnitude, 0)

                delayBackToPosition[subordinate] = targetPos
                animateMove(subordinate, targetPos)
            }

            // bring target window to the top
            windowOrder.remove(window)
            windowOrder.add(window)

            // if no window overlapping shake it
            if (delayBackToPosition.isEmpty()) {
                shake(window)
                return@async
            }

            // animate back to position
            delayBackToPosition.forEach {
                animateMove(it.key, it.value.times(-1f))
            }
            // endregion
        }

        true
    }

    fun close(window: Window): Boolean {
        windowOrder.remove(window)
        windowPosition.remove(window)
        return true
    }

    suspend fun move(window: Window, by: Offset) {
        val currentPos = windowPosition[window] ?: return
        val newPos = currentPos + by.let { IntOffset(x = it.x.toInt(), y = it.y.toInt()) }
        windowPosition[window] = newPos

        // bring to front if not on top
        also {
            if (window != windowOrder.last())
                bringToFront(window)
        }
    }

    suspend fun animateMove(window: Window, by: IntOffset, durationMillis: Int = 150) {
        val frameDuration = 1000L / 60L // = 60 fps
        val steps = (durationMillis / frameDuration).toInt()

        val start = windowPosition[window] ?: return
        val end = start + by

        for (i in 1..steps) {
            val t = i / steps.toFloat()
            val interpolated = IntOffset(
                x = (start.x + by.x * t).toInt(),
                y = (start.y + by.y * t).toInt()
            )
            windowPosition[window] = interpolated
            delay(frameDuration)
        }

        // Ensure final position is accurate
        windowPosition[window] = end
    }

    suspend fun shake(window: Window) {
        if (shaker.contains(window)) return

        shaker.add(window)
        delay(500)
        shaker.remove(window)
    }

    fun requestWindow(application: Application): Window {
        val window = Window(this, application)
        windows.add(window)
        return window
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Draw(
        modifier: Modifier = Modifier.Companion,
        safeContentPadding: PaddingValues,
    ) {
        val scope = rememberCoroutineScope()
        val density = LocalDensity.current

        LaunchedEffect(density.density) {
            screenDensity = density.density
        }

        Box(
            modifier = Modifier
                .padding(safeContentPadding)
                .onSizeChanged {
                    if (it.height <= 0) return@onSizeChanged
                    playGroundSize = it
                }
                .then(modifier)
        ) {
            windowPosition.forEach {
                val window = it.key
                val zIndex = windowOrder.indexOf(it.key).toFloat()
                val position = it.value
                val requestedSize = it.key.expectedSize
                val isOnTop = windowOrder.last() == it.key

                // shaker
                val shakerAnimation = shakerAnimation(shaker.contains(window))

                window.Draw(
                    modifier = Modifier.Companion
                        .size(requestedSize)
                        .offset { position }
                        .onGloballyPositioned {
                            val yOffset = it.positionInParent().y.takeIf { it < 0 } ?: return@onGloballyPositioned
                            scope.launch {
                                move(window, Offset(x = 0f, y = yOffset.absoluteValue))
                            }
                        }
                        .zIndex(zIndex)
                        .rotate(shakerAnimation.value)
                        .shadow(
                            if (isOnTop) 4.dp else 1.dp,
                            RoundedCornerShape(16.dp)
                        )
                        .onClick {
                            scope.launch {
                                if (!isOnTop)
                                    bringToFront(it.key, true)
                            }
                        }
                )
            }
        }
    }
}

@Composable
private fun shakerAnimation(isShaking: Boolean): State<Float> {
    if (!isShaking) return derivedStateOf { 0f }

    val infiniteTransition = rememberInfiniteTransition()
    return infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
}