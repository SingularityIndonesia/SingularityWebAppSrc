package org.singularityuniverse.console.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import appsketch.composeapp.generated.resources.Res
import appsketch.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource

class ToolItem(
    val name: String,
    val category: List<String>,
    val members: List<ToolItem> = emptyList(),
    val componentSpec: ComponentSpec? = null,
)

class ComponentSpec {

}

@Composable
fun ToolItem(modifier: Modifier = Modifier, item: ToolItem) {
    val expandMember = mutableStateOf(false)

    Card(
        modifier = modifier,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = item.name,
                    style = MaterialTheme.typography.labelLarge
                )
                if (item.members.isNotEmpty()) {
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            expandMember.value = !expandMember.value
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.compose_multiplatform),
                            contentDescription = null
                        )
                    }
                }
                Spacer(Modifier.size(8.dp))
            }

            AnimatedVisibility(visible = expandMember.value) {

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Spacer(Modifier.size(4.dp))

                    item.members.map {
                        ToolItem(
                            modifier = Modifier.fillMaxWidth()
                                .padding(start = 8.dp, end = 4.dp),
                            item = it
                        )
                    }

                    Spacer(Modifier.size(4.dp))
                }
            }
        }
    }
}