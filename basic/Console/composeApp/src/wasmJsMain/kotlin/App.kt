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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import org.singularityuniverse.console.utils.windowId

@Composable
fun App() {
    MaterialTheme {
        val contentColor = Color(0xff097233)
        val textStyle = MaterialTheme.typography.bodyMedium

        CompositionLocalProvider(
            LocalTextStyle provides textStyle,
            LocalContentColor provides contentColor
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
            Text(
                text = log
                    .replace("PROMPT:", ("$windowId js >")),
                modifier = Modifier.fillMaxWidth()
            )
        }
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
    }

    LaunchedEffect(Unit) {
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
            text = "$windowId js > ",
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