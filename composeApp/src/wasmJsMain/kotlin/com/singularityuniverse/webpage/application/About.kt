package com.singularityuniverse.webpage.application

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.singularityuniverse.webpage.core.Application
import org.jetbrains.compose.resources.painterResource
import singularityuniverse.composeapp.generated.resources.Res
import singularityuniverse.composeapp.generated.resources.logo_of_singularity_indonesia

class About : Application(){
    override val title: String = "About"
    override val defaultMinSize: DpSize = DpSize(300.dp, 400.dp)

    @Composable
    override fun Draw(modifier: Modifier) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.size(72.dp))
            Image(
                modifier = Modifier.size(90.dp),
                painter = painterResource(Res.drawable.logo_of_singularity_indonesia),
                contentDescription = null,
            )
            Spacer(Modifier.size(24.dp))
            Text(
                text = "Singularity Universe",
                style = MaterialTheme.typography.h6
            )
            Text(
                text = "v0.0.0-proto",
                style = MaterialTheme.typography.overline
            )
        }
    }
}