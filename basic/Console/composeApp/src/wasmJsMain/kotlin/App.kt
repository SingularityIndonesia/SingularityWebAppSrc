package org.singularityuniverse.console

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.browser.window

val windowId = window.frameElement?.id

@Composable
fun App() {
    MaterialTheme {
        val logs = remember { mutableStateListOf<String>() }
        val focusRequester = remember { FocusRequester() }
        val listState = rememberLazyListState()

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(logs) { log ->
                Text(
                    text = log,
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item(focusRequester) {
                PromptInput(
                    focusRequester = focusRequester
                ) { command ->
                    // Add the command to logs
                    logs.add("$windowId > $command")

                    // Process the command
                    when (command.lowercase().trim()) {
                        "clear" -> logs.clear()
                        "" -> { /* Do nothing for empty commands */
                        }

                        else -> {
                            // Execute JavaScript in browser console
                            val result = executeJavaScript(command)
                            logs.add(result)
                        }
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            listState.scrollToItem(logs.size)
            focusRequester.requestFocus()
        }
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
            text = "$windowId > ",
            color = Color.Green,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp
        )

        BasicTextField(
            value = promptBuffer.value,
            onValueChange = { promptBuffer.value = it },
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            cursorBrush = SolidColor(Color.Green),
            textStyle = LocalTextStyle.current.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                color = Color.Green,
            ),
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