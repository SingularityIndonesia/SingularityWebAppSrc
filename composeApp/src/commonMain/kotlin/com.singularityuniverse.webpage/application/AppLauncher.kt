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
package com.singularityuniverse.webpage.application

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.singularityuniverse.webpage.core.Application
import com.singularityuniverse.webpage.core.design.Arrangement
import com.singularityuniverse.webpage.core.design.Padding
import com.singularityuniverse.webpage.core.design.SpaceMedium
import org.jetbrains.compose.resources.painterResource
import singularityuniverse.composeapp.generated.resources.Res
import singularityuniverse.composeapp.generated.resources.ic_launcher_60

class AppLauncher : Application() {
    override val title: String = "App Launcher (Under development)"
    override val defaultMinSize: DpSize = DpSize(500.dp, 500.dp)
    private val iconSize = 72.dp

    private val appList = listOf(
        About(),
        Calculator(),
        GameOfLife(),
        Magician(),
        NumberRecognition()
    ).sortedBy { it.title }

    @Composable
    override fun Draw(modifier: Modifier) {
        LazyVerticalGrid(
            modifier = Modifier.wrapContentSize(),
            contentPadding = Padding.Medium,
            verticalArrangement = Arrangement.MediumSpace,
            horizontalArrangement = Arrangement.MediumSpace,
            columns = GridCells.Adaptive(iconSize + 8.dp),
        ) {
            items(appList) {
                Column(
                    modifier = Modifier.width(iconSize + 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    it.Icon(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(iconSize)
                            .border(BorderStroke(.5.dp, Color.LightGray), RoundedCornerShape(8.dp)),
                        onClick = {

                        }
                    )
                    SpaceMedium()
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = it.title,
                        maxLines = 2,
                        style = MaterialTheme.typography.caption,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

    @Composable
    override fun Icon(modifier: Modifier, onClick: () -> Unit) {
        IconButton(
            modifier = modifier,
            onClick = onClick
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(Res.drawable.ic_launcher_60),
                contentDescription = null
            )
        }
    }
}