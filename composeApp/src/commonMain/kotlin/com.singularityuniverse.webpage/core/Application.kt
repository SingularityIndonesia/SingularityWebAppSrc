package com.singularityuniverse.webpage.core

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import org.jetbrains.compose.resources.painterResource
import singularityuniverse.composeapp.generated.resources.Res
import singularityuniverse.composeapp.generated.resources.compose_multiplatform
import singularityuniverse.composeapp.generated.resources.logo_of_singularity_indonesia

abstract class Application {
    // region ui config
    abstract val title: String
    abstract val defaultMinSize: DpSize
    // end-region

    @Composable
    abstract fun Draw(modifier: Modifier = Modifier)

    @Composable
    open fun Icon(modifier: Modifier, onClick: () -> Unit) {
        IconButton(
            modifier = modifier,
            onClick = onClick
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(Res.drawable.logo_of_singularity_indonesia),
                contentDescription = null
            )
        }
    }
}