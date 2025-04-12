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
package com.singularityuniverse.webpage.core

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.launch

class Window(
    val wm: WindowManager,
    val app: Application,
) {
    val title = mutableStateOf(app.title)
    fun setTitle(title: String) {
        this.title.value = title
    }

    // using defaultMinSize as expected size because the size it requested
    // might not be able tobe provided by the WindowManager
    val expectedSize = app.defaultMinSize
    // updated on size changed
    val currentSize = mutableStateOf(IntSize.Zero)
    val center = currentSize.value.let { IntOffset(it.width, it.height) }.div(2f)

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Draw(modifier: Modifier = Modifier) {
        val scope = rememberCoroutineScope()

        Scaffold(
            modifier = modifier
                .onSizeChanged { currentSize.value = it },
            topBar = {
                TopBar(
                    modifier = Modifier
                        .onDrag {
                            scope.launch {
                                // fixme: make window manager inherit coordinator
                                wm.move(this@Window, it)
                            }
                        },
                    title = title.value,
                    onClose = { wm.close(this@Window) }
                )
            }
        ) { padding ->
            app.Draw(
                modifier = Modifier.Companion
                    .padding(padding)
                    .fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TopBar(
    modifier: Modifier,
    title: String,
    onClose: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onClose
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null
                )
            }
        },
        title = {
            Text(title)
        }
    )
}