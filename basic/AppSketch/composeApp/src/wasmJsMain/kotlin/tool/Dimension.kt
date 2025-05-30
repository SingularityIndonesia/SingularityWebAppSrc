package org.singularityuniverse.console.tool

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Number.pxToDp(): Dp {
    val density = LocalDensity.current
    return (this.toFloat() / density.density).dp
}