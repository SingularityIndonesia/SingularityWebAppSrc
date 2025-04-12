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
import kotlinx.coroutines.*
import kotlin.math.absoluteValue

class WindowManager {
    var playGroundSize: IntSize? = null
    var screenDensity: Float? = null
    val windows = mutableStateListOf<Window>()
    val coordinator = WindowCoordinator(this)

    suspend fun open(window: Window): Boolean {
        if (coordinator.bringToFront(window, true)) {
            return true
        }

        // wait till playground ready
        if (playGroundSize == null || screenDensity == null) {
            delay(300)
            return open(window)
        }

        val center = playGroundSize!!.center.minus(
            window.expectedSize.let {
                IntOffset(
                    x = (it.width.value * (screenDensity ?: 1f) / 2).fastRoundToInt(),
                    y = (it.height.value * (screenDensity ?: 1f) / 2).fastRoundToInt(),
                )
            }
        )
        // fixme: move control to coordinator
        coordinator.windowPosition[window] = center
        coordinator.windowOrder.add(window)
        return true
    }

    fun close(window: Window): Boolean {
        // fixme: move complexcity to coordinator
        coordinator.windowOrder.remove(window)
        coordinator.windowPosition.remove(window)
        return true
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
            // fixme: refactor to `coordinator.windowCoordinate.forEach`
            coordinator.windowPosition.forEach {
                val window = it.key
                // fixme: make facade in coordinator
                val zIndex = coordinator.windowOrder.indexOf(it.key).toFloat()
                val position = it.value
                val requestedSize = it.key.expectedSize
                // fixme: make facade in coordinator
                val isOnTop = coordinator.windowOrder.last() == it.key

                // shaker
                // fixme: feels wrong
                val shakerAnimation = coordinator.shakerAnimator(window)

                window.Draw(
                    modifier = Modifier.Companion
                        .size(requestedSize)
                        .offset { position }
                        .onGloballyPositioned {
                            val yOffset = it.positionInParent().y.takeIf { it < 0 } ?: return@onGloballyPositioned
                            scope.launch {
                                coordinator.move(window, Offset(x = 0f, y = yOffset.absoluteValue))
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
                                    coordinator.bringToFront(it.key, true)
                            }
                        }
                )
            }
        }
    }
}