package org.singularityuniverse.console.pane

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import org.singularityuniverse.console.tool.PaddingValues

@Composable
fun Playground(
    modifier: Modifier
) {
    PlaygroundScaffold(
        modifier = modifier,
        leftDrawer = {
            LeftDrawer(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
            )
        },
        scene = {

        },
        rightDrawer = {

        }
    )
}

@Composable
fun PlaygroundScaffold(
    modifier: Modifier = Modifier,
    leftDrawer: @Composable () -> Unit,
    scene: @Composable (padding: PaddingValues) -> Unit,
    rightDrawer: @Composable () -> Unit,
) {
    val leftDrawerSize = remember { mutableStateOf(IntSize.Zero) }
    val rightDrawerSize = remember { mutableStateOf(IntSize.Zero) }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.onSizeChanged {
                leftDrawerSize.value = it
            }
        ) {
            leftDrawer.invoke()
        }
        scene.invoke(
            PaddingValues(
                start = leftDrawerSize.value,
                end = rightDrawerSize.value,
            )
        )
        Box {
            rightDrawer.invoke()
        }
    }
}

@Composable
fun LeftDrawer(modifier: Modifier = Modifier) {
    val selectedTabIndex = remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                shape = RoundedCornerShape(16.dp)
            )
            .background(MaterialTheme.colorScheme.surface)
    ) {
        TabRow(selectedTabIndex = selectedTabIndex.value) {
            Tab(
                selected = selectedTabIndex.value == 0,
                onClick = {
                    selectedTabIndex.value = 0
                }
            ) {
                Text(
                    modifier = Modifier.padding(
                        vertical = 8.dp,
                        horizontal = 16.dp
                    ),
                    text = "Tools"
                )
            }
        }
        if (selectedTabIndex.value == 0)
            ToolsPane(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
    }
}