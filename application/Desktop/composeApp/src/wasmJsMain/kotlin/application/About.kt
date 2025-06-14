/**
 * Copyright (C) 2025  stefanus.ayudha@gmail.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package application

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import core.Application
import core.design.Overline
import core.design.SpaceExtraLarge
import core.design.TextIcon
import desktop.composeapp.generated.resources.Res
import desktop.composeapp.generated.resources.logo_of_singularity_indonesia
import lib.open
import org.jetbrains.compose.resources.painterResource

class About : Application() {
    override val title: String = "About"
    override val defaultMinSize: DpSize = DpSize(300.dp, 460.dp)

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Draw(modifier: Modifier) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.size(94.dp))
            Image(
                modifier = Modifier.size(90.dp),
                painter = painterResource(Res.drawable.logo_of_singularity_indonesia),
                contentDescription = null,
            )
            SpaceExtraLarge()
            Text(
                text = "Singularity Universe",
                style = MaterialTheme.typography.h6
            )
            Overline("v0.0.0-proto")
            Spacer(Modifier.height(62.dp))
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = "100% Kotlin 100% Jetpack Compose 100% Awesome",
                style = MaterialTheme.typography.overline,
                textAlign = TextAlign.Center
            )
            Row(
                horizontalArrangement = spacedBy(16.dp)
            ) {
                TextButton(
                    onClick = {
                        open("/README.md")
                    }
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = "More Info",
                    )
                }

                TextButton(
                    onClick = {
                        open("https://github.com/SingularityIndonesia/SingularityWebAppSrc")
                    }
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = "Source Code",
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Icon(modifier: Modifier, onClick: () -> Unit) {
        TextIcon(modifier = modifier, text = "A", onClick = onClick)
    }
}