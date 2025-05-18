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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import core.Application
import core.design.SpaceExtraLarge
import lib.open
import org.jetbrains.compose.resources.painterResource
import singularityuniverse.composeapp.generated.resources.*

class Magician : Application() {
    override val title: String = "Magician Profile"
    override val defaultMinSize: DpSize = DpSize(300.dp, 500.dp)

    @Composable
    override fun Draw(modifier: Modifier) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SpaceExtraLarge()
                Image(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(120.dp)),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                    painter = painterResource(Res.drawable.magician_profile_pict),
                    contentDescription = null
                )
                SpaceExtraLarge()
                Text(
                    text = "Stefanus Ayudha",
                    style = MaterialTheme.typography.h5
                )
                Text(
                    text = "Expert Kotlin Developer",
                    style = MaterialTheme.typography.body2
                )

                SpaceExtraLarge()
                TextButton(
                    onClick = {
                        open("https://wa.me/+62895340952006")
                    }
                ) {
                    Text("Contact the Magician")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            open("mailto:stefanus.ayudha@gmail.com")
                        }
                    ) {
                        Image(
                            modifier = Modifier.requiredHeight(22.dp),
                            contentScale = ContentScale.FillHeight,
                            painter = painterResource(Res.drawable.ic_gmail_24),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {
                            open("https://github.com/stefanusayudha")
                        }
                    ) {
                        Image(
                            modifier = Modifier.requiredHeight(24.dp),
                            contentScale = ContentScale.FillHeight,
                            painter = painterResource(Res.drawable.ic_github_24),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {
                            open("https://www.linkedin.com/in/stefanus-ayudha-447a98b5/")
                        }
                    ) {
                        Image(
                            modifier = Modifier.requiredHeight(24.dp),
                            contentScale = ContentScale.FillHeight,
                            painter = painterResource(Res.drawable.ic_linkedin_24),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {
                            open("https://wa.me/+62895340952006")
                        }
                    ) {
                        Image(
                            modifier = Modifier.requiredHeight(25.dp),
                            contentScale = ContentScale.FillHeight,
                            painter = painterResource(Res.drawable.ic_whatsapp_24),
                            contentDescription = null
                        )
                    }
                }
                SpaceExtraLarge()
            }
        }
    }
}