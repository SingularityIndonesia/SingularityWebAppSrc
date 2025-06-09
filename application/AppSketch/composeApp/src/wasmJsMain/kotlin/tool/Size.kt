package org.singularityuniverse.console.tool

import androidx.compose.ui.unit.IntSize

infix operator fun IntSize.plus(next: IntSize): IntSize {
    return IntSize(
        width = width + next.width,
        height = height + next.height
    )
}