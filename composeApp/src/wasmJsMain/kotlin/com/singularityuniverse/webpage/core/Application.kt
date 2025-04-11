package com.singularityuniverse.webpage.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize

abstract class Application {
    // region ui config
    abstract val title: String
    abstract val defaultMinSize: DpSize
    // end-region

    @Composable
    abstract fun Draw(modifier: Modifier = Modifier.Companion)
}