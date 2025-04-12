package com.singularityuniverse.webpage.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset

interface WindowCoordinator {
    val windowPosition: SnapshotStateMap<Window, IntOffset>
    val windowOrder: SnapshotStateList<Window>
    val shaker: SnapshotStateList<Window>

    suspend fun move(window: Window, by: Offset)
    suspend fun animateMove(window: Window, by: IntOffset, durationMillis: Int = 150)
    suspend fun shake(window: Window)
    suspend fun bringToFront(window: Window, animated: Boolean = false): Boolean

    @Composable
    fun shakerAnimator(window: Window): State<Float>
}

