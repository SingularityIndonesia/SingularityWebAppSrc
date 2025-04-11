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
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class WindowManager {
    var playGroundSize: IntSize? = null
    var screenDensity: Float? = null
    val windows = mutableStateListOf<Window>()
    val windowPosition = mutableStateMapOf<Window, IntOffset>()
    val windowOrder = mutableStateListOf<Window>()
    val shaker = mutableStateListOf<Window>()

    suspend fun open(window: Window): Boolean {
        if (windowOrder.contains(window)) {
            return bringToFront(window)
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

    fun bringToFront(window: Window): Boolean {
        windowOrder.remove(window)
        windowOrder.add(window)
        return true
    }

    fun close(window: Window): Boolean {
        windowOrder.remove(window)
        windowPosition.remove(window)
        return true
    }

    fun move(window: Window, by: Offset) {
        val currentPos = windowPosition[window] ?: return
        val newPos = currentPos + by.let { IntOffset(x = it.x.toInt(), y = it.y.toInt()) }
        windowPosition[window] = newPos

        // bring to front if not on top
        also {
            if (window != windowOrder.last())
                bringToFront(window)
        }
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

                // region window shaker
                val shouldShaking = shaker.contains(window)
                val infiniteTransition = rememberInfiniteTransition()
                val shaker = infiniteTransition.animateFloat(
                    initialValue = if (shouldShaking) -10f else 0f,
                    targetValue = if (shouldShaking) 10f else 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(150, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
                // endregion

                window.Draw(
                    modifier = Modifier.Companion
                        .size(requestedSize)
                        .offset { position }
                        .onGloballyPositioned {
                            val yOffset = it.positionInParent().y.takeIf { it < 0 } ?: return@onGloballyPositioned
                            move(window, Offset(x = 0f, y = yOffset.absoluteValue))
                        }
                        .zIndex(zIndex)
                        .rotate(shaker.value)
                        .shadow(
                            if (isOnTop) 4.dp else 1.dp,
                            RoundedCornerShape(16.dp)
                        )
                        .onClick {
                            val isOnTheFKNGTop = windowOrder.lastOrNull() == it
                            if (isOnTheFKNGTop)
                                scope.launch { shake(it) }

                            bringToFront(it.key)
                        }
                )
            }
        }
    }
}