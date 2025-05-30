package org.singularityuniverse.console.tool

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection

infix operator fun PaddingValues.plus(next: PaddingValues): PaddingValues {
    return PaddingValues(
        start = calculateStartPadding(layoutDirection = LayoutDirection.Ltr),
        top = calculateTopPadding(),
        end = calculateEndPadding(layoutDirection = LayoutDirection.Rtl),
        bottom = calculateBottomPadding(),
    )
}

@Composable
fun PaddingValues(
    start: IntSize = IntSize.Zero,
    top: IntSize = IntSize.Zero,
    end: IntSize = IntSize.Zero,
    bottom: IntSize = IntSize.Zero
): PaddingValues {
    return PaddingValues(
        start = start.width.pxToDp(),
        top = top.height.pxToDp(),
        end = end.width.pxToDp(),
        bottom = bottom.height.pxToDp(),
    )
}