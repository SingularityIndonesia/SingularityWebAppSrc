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
package application

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import core.Application
import core.Window
import core.WindowManagerImpl
import core.design.Arrangement
import lib.`timeInMMMM_dd_HH:mm`
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class Desktop : Application() {
    override val title: String = "Desktop"
    override val defaultMinSize: DpSize = DpSize.Unspecified
    private val windowManager = WindowManagerImpl()
    private val applications = mutableStateListOf(
        AppLauncher(), NumberRecognition(), Calculator(), GameOfLife(), Magician(), About()
    )
    private val windows = mutableStateListOf<Window>()

    suspend fun openApp(application: Application) {
        if (!applications.contains(application)) {
            applications.add(application)
        }

        val window = windows.firstOrNull { it.app == application } ?: run {
            val w = windowManager.requestWindow(application)
            windows += w
            w
        }

        windowManager.open(window)
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Draw(modifier: Modifier) {
        // fixme: make facade
        val topApplication = windowManager.windowOrder.lastOrNull()?.app
        val scope = rememberCoroutineScope()
        var showRightDrawer by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            openApp(applications.firstOrNull { it is About } ?: return@LaunchedEffect)
        }

        Scaffold(
            modifier = modifier
                .padding(6.dp)
                .clip(RoundedCornerShape(16.dp))
                .fillMaxSize(),
            topBar = {
                StatusBar(
                    modifier = Modifier
                        .height(25.dp),
                    context = topApplication?.title.orEmpty(),
                    onTitleClicked = {},
                    onDateClicked = {
                        showRightDrawer = !showRightDrawer
                    }
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    BottomAppBar(
                        modifier = Modifier.padding(8.dp),
                        appList = applications,
                        onAppClicked = { app ->
                            scope.launch {
                                openApp(app)
                            }
                        }
                    )
                }
            },
            backgroundColor = Color.Blue.copy(alpha = .7f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onClick {
                        // clear desktop
                        showRightDrawer = false
                    }
            ) {
                windowManager.Draw(
                    modifier = Modifier
                        .fillMaxSize(),
                    safeContentPadding = it
                )
                AnimatedVisibility(
                    visible = showRightDrawer,
                    modifier = Modifier.align(Alignment.TopEnd),
                    enter = slideInHorizontally(initialOffsetX = { it }),
                    exit = slideOutHorizontally(targetOffsetX = { it })
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .width(300.dp)
                            .background(Color.White.copy(alpha = 0.7f))
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = .1f))
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            text = "No widget Available"
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StatusBar(
    modifier: Modifier,
    context: String,
    onTitleClicked: () -> Unit,
    onDateClicked: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = .7f))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusBarTitle(context, onTitleClicked)
        Spacer(Modifier.weight(1f))
        StatusBarDate(onDateClicked)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StatusBarTitle(text: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    Text(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isHovered) Color.Black.copy(alpha = .1f)
                else Color.Transparent
            )
            .padding(horizontal = 8.dp)
            .hoverable(interactionSource)
            .onClick { onClick.invoke() },
        text = text,
        style = MaterialTheme.typography.body2,
        fontWeight = FontWeight.Bold
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StatusBarDate(onClick: () -> Unit) {
    val scope = rememberCoroutineScope()
    val clock = remember { mutableStateOf("00:00") }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    DisposableEffect(Unit) {
        val job = scope.launch {
            while (this.isActive) {
                clock.value = `timeInMMMM_dd_HH:mm`
                delay(1000)
            }
        }
        onDispose { job.cancel() }
    }
    Text(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isHovered) Color.Black.copy(alpha = .1f)
                else Color.Transparent
            )
            .padding(horizontal = 8.dp)
            .hoverable(interactionSource)
            .onClick { onClick.invoke() },
        text = clock.value,
        style = MaterialTheme.typography.caption,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun BottomAppBar(
    modifier: Modifier = Modifier,
    appList: List<Application>,
    onAppClicked: (Application) -> Unit
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = .7f))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.MediumSpace,
    ) {
        appList.forEach {
            it.Icon(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                onClick = {
                    onAppClicked.invoke(it)
                }
            )
        }
    }
}