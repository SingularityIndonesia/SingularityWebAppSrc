package com.singularityuniverse.webpage.core.design

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.onClick
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextIcon(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colors.background)
            .onClick { onClick.invoke() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier,
            text = text,
            style = MaterialTheme.typography.overline,
            color = MaterialTheme.colors.onBackground,
            fontSize = 32.sp
        )
    }
}