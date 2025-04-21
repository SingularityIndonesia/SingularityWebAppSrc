package com.singularityuniverse.webpage.core.design

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ColumnScope.SpaceSmall() {
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
fun RowScope.SpaceSmall() {
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
fun ColumnScope.SpaceMedium() {
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun RowScope.SpaceMedium() {
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun ColumnScope.SpaceLarge() {
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun RowScope.SpaceLarge() {
    Spacer(modifier = Modifier.width(16.dp))
}

@Composable
fun ColumnScope.SpaceExtraLarge() {
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun RowScope.SpaceExtraLarge() {
    Spacer(modifier = Modifier.width(24.dp))
}