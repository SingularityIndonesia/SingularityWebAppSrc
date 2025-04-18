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

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class WindowManagerImpl : WindowManager, WindowCoordinator by WindowCoordinatorImpl() {
    private val windows: SnapshotStateList<Window> = mutableStateListOf<Window>()
    private val playGroundSize: MutableState<IntSize> = mutableStateOf(IntSize.Companion.Zero)
    private val screenDensity: MutableState<Float> = mutableStateOf(0f)

    override fun requestWindow(application: Application): Window {
        val window = Window(this, application)
        windows.add(window)
        return window
    }

    override suspend fun open(window: Window): Boolean {
        if (bringToFront(window, true)) {
            return true
        }

        // wait till playground drawing ready
        if (playGroundSize.value == IntSize.Companion.Zero || screenDensity.value == 0f) {
            delay(300)
            return open(window)
        }

        // coordinate buffer
        windowPosition[window] = null
        windowOrder.add(window)
        return true
    }

    override fun close(window: Window): Boolean {
        windowOrder.remove(window)
        windowPosition.remove(window)
        return true
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Draw(
        modifier: Modifier,
        safeContentPadding: PaddingValues,
    ) {
        val scope = rememberCoroutineScope()
        val density = LocalDensity.current

        LaunchedEffect(density.density) {
            screenDensity.value = density.density
        }

        Box(
            modifier = Modifier
                .onSizeChanged {
                    if (it.height <= 0) return@onSizeChanged
                    playGroundSize.value = it
                }
                .then(modifier)
        ) {
            // fixme: refactor to `coordinator.windowCoordinate.forEach`
            windowPosition.forEach {
                val window = it.key
                key(window) {
                    // fixme: make facade in coordinator
                    val zIndex = windowOrder.indexOf(it.key).toFloat()
                    val position = it.value ?: getCenterInitialPosition(window, safeContentPadding)
                        // if position is null -> calculate center -> re-initiate position
                        .also { windowPosition[window] = it }

                    val requestedSize = it.key.expectedSize
                    // fixme: make facade in coordinator
                    val isOnTop = windowOrder.last() == it.key

                    // shaker
                    val shakerAnimation = shakerAnimator(window)

                    // region pop up animator
                    var isOpened by remember { mutableStateOf(false) }
                    val popUpAnimator = animateFloatAsState(
                        if (isOpened) 1f else 0f, tween(150, 0, LinearEasing)
                    )

                    LaunchedEffect(isOpened) {
                        if (isOpened) return@LaunchedEffect
                        isOpened = true
                    }
                    // endregion

                    window.Draw(
                        modifier = Modifier.Companion
                            .size(requestedSize)
                            .offset { position }
                            .onGloballyPositioned {
                                val yOffset = it.positionInParent().y.takeIf { it < 0 }
                                    ?: return@onGloballyPositioned

                                scope.launch {
                                    move(window, Offset(x = 0f, y = yOffset.absoluteValue))
                                }
                            }
                            .zIndex(zIndex)
                            .rotate(shakerAnimation.value)
                            .scale(popUpAnimator.value)
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

    fun getCenterInitialPosition(window: Window, safeContentPadding: PaddingValues): IntOffset {
        val screenDensity = this.screenDensity.value.absoluteValue
        val statusBarPadding = safeContentPadding.calculateTopPadding().value * screenDensity
        val bottomBarPadding = safeContentPadding.calculateBottomPadding().value * screenDensity

        val safePlaygroundSize = playGroundSize.value
            .let {
                IntSize(
                    width = it.width,
                    height = (it.height - (statusBarPadding + bottomBarPadding)).toInt()
                )
            }

        val offset = safePlaygroundSize.center.minus(
            window.expectedSize.let {
                IntOffset(
                    x = (it.width.value * screenDensity / 2).fastRoundToInt(),
                    y = ((it.height.value * screenDensity / 2) + statusBarPadding).fastRoundToInt(),
                )
            }
        )

        return offset
    }
}