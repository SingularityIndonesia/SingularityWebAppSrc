package com.singularityuniverse.webpage.core.design

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ColumnScope.Space16() {
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun RowScope.Space16() {
    Spacer(modifier = Modifier.width(16.dp))
}

@Composable
fun ColumnScope.Space24() {
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun RowScope.Space24() {
    Spacer(modifier = Modifier.width(24.dp))
}