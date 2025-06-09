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
    val listItems = mutableStateListOf(*tools.toTypedArray())
    val searchQuery = mutableStateOf("")
    val displayedItems
        get() = listItems
            .filter { item ->
                if (searchQuery.value == "") true
                else item.name.contains(searchQuery.value)
            }
            .let { items ->
                val listCategory = items
                    .fold(emptyList<String>()) { acc, n -> acc + n.category }
                    .distinct()

                listCategory.map { category ->
                    ToolItem(
                        name = category.replaceFirstChar { it.titlecase() } + "s",
                        category = emptyList(),
                        members = items.filter { it.category.contains(category) })
                }
            }
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

val tools = listOf(
    ToolItem(name = "Primary Button", category = listOf("button")),
    ToolItem(name = "Secondary Button", category = listOf("button")),
    ToolItem(name = "Text Button", category = listOf("button")),
    ToolItem(name = "Outline Button", category = listOf("button")),
    ToolItem(name = "Horizontal Layout", category = listOf("container")),
    ToolItem(name = "vertical Layout", category = listOf("container")),
    ToolItem(name = "Box", category = listOf("container")),
    ToolItem(name = "Card", category = listOf("container"))
)

@Preview
@Composable
private fun Preview() {
    ToolsPane(
        modifier = Modifier
            .width(200.dp)
            .fillMaxHeight()
    )
}