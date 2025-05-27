/**
 * Copyright (C) 2025  stefanus.ayudha@gmail.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package core

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.fastRoundToInt
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

class WindowCoordinatorImpl : WindowCoordinator {
    override val windowPosition = mutableStateMapOf<Window, IntOffset?>()
    override val windowOrder = mutableStateListOf<Window>()
    override val shaker = mutableStateListOf<Window>()

    override suspend fun move(window: Window, by: Offset) {
        val currentPos = windowPosition[window] ?: return
        val newPos = currentPos + by.let { IntOffset(x = it.x.fastRoundToInt(), y = it.y.fastRoundToInt()) }
        windowPosition[window] = newPos

        // bring to front if not on top
        also {
            if (window != windowOrder.last())
                bringToFront(window)
        }
    }

    override suspend fun animateMove(window: Window, by: IntOffset, durationMillis: Int) {
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

    override suspend fun shake(window: Window) {
        if (shaker.contains(window)) return

        shaker.add(window)
        delay(500)
        shaker.remove(window)
    }

    private var bringToFrontJob: Deferred<Unit>? = null
    override suspend fun bringToFront(window: Window, animated: Boolean): Boolean = coroutineScope {
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
                        currentWindowStart - subOrdinateEnd - 32f// extra 16 dp
                    }

                    else -> {
                        currentWindowEnd - subOrdinateStart + 32f// extra 16 dp
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
    override fun shakerAnimator(window: Window): State<Float> {
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