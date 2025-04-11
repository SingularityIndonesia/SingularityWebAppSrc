package com.singularityuniverse.webpage.application

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onClick
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singularityuniverse.webpage.core.Application

class Calculator : Application() {
    override val title: String = "Calculator"
    override val defaultMinSize: DpSize = DpSize(400.dp, 500.dp)
    private val log = mutableStateOf("")

    @Composable
    override fun Draw(modifier: Modifier) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Logger(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                log = this@Calculator.log.value
            )

            InputPane(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                onInput = {
                    // on clear
                    if (it == "C") {
                        log.value = log.value.lines().dropLast(1).joinToString("\n")
                        return@InputPane
                    }

                    // on calculate
                    if (it == "=") {
                        val result = calculate(log.value.lines().last())
                        log.value += "\n$result"
                        return@InputPane
                    }

                    // on backspace
                    if (it == "bsp") {
                        val lastLine = log.value
                            .lines()
                            .lastOrNull()
                            ?.toCharArray()
                            ?.dropLast(1)
                            ?.joinToString("")
                            ?: return@InputPane

                        log.value = log.value
                            .lines()
                            .dropLast(1)
                            .plus(lastLine)
                            .joinToString("\n")

                        return@InputPane
                    }

                    if (it == "AC") {
                        log.value = ""
                        return@InputPane
                    }

                    log.value += it
                }
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Icon(modifier: Modifier, onClick: () -> Unit) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colors.secondary)
                .onClick { onClick.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier,
                text = "C",
                style = MaterialTheme.typography.overline,
                color = MaterialTheme.colors.onSecondary,
                fontSize = 32.sp
            )
        }
    }

    private fun calculate(string: String): String {
        return "Hello World"
    }
}

@Composable
private fun Logger(
    modifier: Modifier = Modifier,
    log: String
) {
    val state = rememberLazyListState()
    val lines = remember(log) { log.lines() }

    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = PaddingValues(8.dp),
        horizontalAlignment = Alignment.End,
    ) {
        items(lines.size) {
            val line = lines[it]
            Text(
                text = line,
                textAlign = TextAlign.End
            )
        }
    }

    LaunchedEffect(lines) {
        state.animateScrollToItem(lines.size - 1)
    }
}

@Composable
private fun InputPane(
    modifier: Modifier = Modifier,
    onInput: (String) -> Unit,
) {
    val inputs = remember {
        listOf(
            "1", "2", "3", "+",
            "4", "5", "6", "-",
            "7", "8", "9", "bsp",
            "AC", "0", "C", "="
        )
    }
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(4),
    ) {
        items(inputs.size) {
            val item = inputs[it]
            IconButton(
                onClick = {
                    onInput.invoke(item)
                }
            ) {
                if (item == "bsp") {
                    Icon(
                        modifier = Modifier.size(12.dp),
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null
                    )
                    return@IconButton
                }

                Text(item)
            }
        }
    }
}