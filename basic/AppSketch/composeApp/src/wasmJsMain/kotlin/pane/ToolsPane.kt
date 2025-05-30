package org.singularityuniverse.console.pane

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.singularityuniverse.console.component.ToolItem

class ToolsPaneState() {
    val searchQuery = mutableStateOf("")
    val displayedItems = mutableStateListOf(
        ToolItem(name = "Scaffold"),
        ToolItem(name = "Layout"),
        ToolItem(name = "Atoms"),
        ToolItem(name = "Molecule"),
    )
}

@Composable
fun rememberToolsPaneState(): ToolsPaneState {
    val state = remember { ToolsPaneState() }
    return state
}

@Composable
fun ToolsPane(
    modifier: Modifier = Modifier,
    state: ToolsPaneState = rememberToolsPaneState()
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(state.displayedItems) { item ->
            ToolItem(modifier = Modifier.fillMaxWidth(), item = item)
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ToolsPane(
        modifier = Modifier
            .width(200.dp)
            .fillMaxHeight()
    )
}