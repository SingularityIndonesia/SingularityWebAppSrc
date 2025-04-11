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

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Draw(modifier: Modifier = Modifier.Companion) {
        val scope = rememberCoroutineScope()

        Scaffold(
            modifier = modifier,
            topBar = {
                TopBar(
                    modifier = Modifier.Companion
                        .onDrag {
                            scope.launch {
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