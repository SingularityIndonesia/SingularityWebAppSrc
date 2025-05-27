package org.singularityuniverse.console

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import application.Desktop

@Composable
fun App() {
    val desktop = remember { Desktop() }
    MaterialTheme {
        desktop.Draw(modifier = Modifier.fillMaxSize())
    }
}