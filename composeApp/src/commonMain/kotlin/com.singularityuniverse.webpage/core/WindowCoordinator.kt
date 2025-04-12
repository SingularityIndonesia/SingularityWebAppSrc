package com.singularityuniverse.webpage.core

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.fastRoundToInt
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

class WindowCoordinator(private val wm: WindowManager) {
    val windowPosition = mutableStateMapOf<Window, IntOffset>()
    val windowOrder = mutableStateListOf<Window>()
    val shaker = mutableStateListOf<Window>()

    suspend fun move(window: Window, by: Offset) {
        val currentPos = windowPosition[window] ?: return
        val newPos = currentPos + by.let { IntOffset(x = it.x.fastRoundToInt(), y = it.y.fastRoundToInt()) }
        windowPosition[window] = newPos

        // bring to front if not on top
        also {
            if (window != windowOrder.last())
                bringToFront(window)
        }
    }

    suspend fun animateMove(window: Window, by: IntOffset, durationMillis: Int = 150) {
        val frameDuration = 1000L / 60L // = 60 fps
        val steps = durationMillis / frameDuration

        val start = windowPosition[window] ?: return
        val end = start + by

        for (i in 1..steps) {
            val t = i / steps.toFloat()
            val interpolated = IntOffset(
                x = (start.x + by.x * t).fastRoundToInt(),
                y = (start.y + by.y * t).fastRoundToInt()
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

    private var bringToFrontJob: Deferred<Unit>? = null
    suspend fun bringToFront(window: Window, animated: Boolean = false): Boolean = coroutineScope {
        // if not registered -> false
        if (!windowOrder.contains(window)) {
            return@coroutineScope false
        }

        // if on top -> shake
        val isOnTheFKNGTop = windowOrder.lastOrNull() == window
        if (isOnTheFKNGTop) {
            shake(window)
            return@coroutineScope true
        }

        // if not animated -> just show
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

                    (subOrdinateCenter - currentWindowCenter)
                        .takeIf { it != 0 }
                        ?.let { it / it.absoluteValue } ?: 0
                } ?: return@forEach // ignore if not overlapping

                val overlapMagnitude = when (overlapDirection) {
                    -1 -> {
                        currentWindowStart - subOrdinateEnd - ((wm.screenDensity?.absoluteValue
                            ?: 1f) * 16)// extra 16 dp
                    }

                    else -> {
                        currentWindowEnd - subOrdinateStart + ((wm.screenDensity?.absoluteValue
                            ?: 1f) * 16)// extra 16 dp
                    }
                }.fastRoundToInt()

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

    @Composable
    fun shakerAnimator(window: Window): State<Float> {
        if (!shaker.contains(window)) return derivedStateOf { 0f }

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
}
