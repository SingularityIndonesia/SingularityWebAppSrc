package org.singularityuniverse.console.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class ToolItem(val name: String)

@Composable
fun ToolItem(modifier: Modifier = Modifier, item: ToolItem) {
    Card(modifier = modifier) {
        Box(modifier = Modifier.padding(8.dp)) {
            Text(item.name)
        }
    }
}