package com.singularityuniverse.webpage.application

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singularityuniverse.webpage.core.Application
import org.jetbrains.compose.resources.painterResource
import org.w3c.dom.Text
import singularityuniverse.composeapp.generated.resources.Res
import singularityuniverse.composeapp.generated.resources.logo_of_singularity_indonesia

class About : Application() {
    override val title: String = "About"
    override val defaultMinSize: DpSize = DpSize(300.dp, 400.dp)

    @OptIn(ExperimentalFoundationApi::class)
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
            Spacer(Modifier.height(8.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.secondary,
                    contentColor = MaterialTheme.colors.onSecondary
                ),
                onClick = {
                    kotlinx.browser.window.open("https://github.com/SingularityIndonesia/SingularityWebAppSrc")
                }
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = "Source Code",
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Icon(modifier: Modifier, onClick: () -> Unit) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colors.surface)
                .onClick { onClick.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier,
                text = "A",
                style = MaterialTheme.typography.overline,
                color = MaterialTheme.colors.onSurface,
                fontSize = 32.sp
            )
        }
    }
}