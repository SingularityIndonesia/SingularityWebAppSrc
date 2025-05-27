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

package org.singularityuniverse.console

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.singularityuniverse.console.utils.windowId

private val GREEN = Color(0xff097233)
private val BLUE = Color(0xff095c9a)
private val RED = Color(0xffa92426)
private val WHITE = Color(0xffb1adbf)

@Composable
fun App() {
    MaterialTheme {
        val textStyle = MaterialTheme.typography.bodyMedium

        CompositionLocalProvider(
            LocalTextStyle provides textStyle,
            LocalContentColor provides GREEN
        ) {
            SelectionContainer {
                Shell()
            }
        }
    }
}

@Composable
fun Shell() {
    val console = remember { Console() }
    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item { Text("Welcome to Singularity Multi Microsite Jetpack Compose Project.") }
        item { Text("Type `help()` to see available tools or `info()` for more info.") }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        items(console.logs) { log ->
            val text = buildAnnotatedString {
                if (log.contains("PROMPT: ")) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = BLUE)) {
                        append("$windowId js ")
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = WHITE)) {
                        append("> ")
                    }
                }

                val remainingText = log.split("PROMPT: ").lastOrNull().orEmpty()

                val style = when {
                    remainingText.contains("ERROR: ") -> SpanStyle(fontWeight = FontWeight.Bold, color = RED)
                    else -> SpanStyle(fontWeight = FontWeight.Normal, color = LocalContentColor.current)
                }

                withStyle(style) {
                    append(remainingText)
                }
            }

            Text(
                text = text,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (!console.isProcessing.value)
            item(focusRequester) {
                PromptInput(
                    focusRequester = focusRequester
                ) { command ->
                    // Add the command to logs

                    // Process the command
                    when (command.lowercase().trim()) {
                        "clear()" -> console.logs.clear()
                        "" -> {}
                        else -> console.eval(command)
                    }
                }
            }
        else
            item {
                Text(
                    text = "executing..",
                    modifier = Modifier.fillMaxWidth()
                )
            }
    }

    LaunchedEffect(console.logs.size) {
        listState.scrollToItem(console.logs.size)
        focusRequester.requestFocus()
    }
}

@Composable
private fun PromptInput(
    focusRequester: FocusRequester,
    onSubmit: (command: String) -> Unit,
) {
    val promptBuffer = remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = BLUE)) {
                    append("$windowId js ")
                }
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = WHITE)) {
                    append("> ")
                }
            },
            style = LocalTextStyle.current
        )

        BasicTextField(
            value = promptBuffer.value,
            onValueChange = { promptBuffer.value = it },
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            cursorBrush = SolidColor(LocalContentColor.current),
            textStyle = LocalTextStyle.current
                .copy(color = LocalContentColor.current),
            decorationBox = { innerTextField ->
                innerTextField()
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    onSubmit.invoke(promptBuffer.value)

                    // Clear the prompt buffer after executing
                    promptBuffer.value = ""
                }
            ),
            singleLine = true,
        )
    }
}