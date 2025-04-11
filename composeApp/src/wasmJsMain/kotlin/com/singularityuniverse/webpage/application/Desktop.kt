package com.singularityuniverse.webpage.application

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.singularityuniverse.webpage.core.Application
import com.singularityuniverse.webpage.core.Window
import com.singularityuniverse.webpage.core.WindowManager
import kotlinx.coroutines.launch

class Desktop : Application() {
    override val title: String = "Desktop"
    override val defaultMinSize: DpSize = DpSize.Unspecified
    private val windowManager = WindowManager()
    private val applications = mutableStateListOf<Application>(Calculator(), About(), Calculator())
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

    @Composable
    override fun Draw(modifier: Modifier) {
        val topApplication = windowManager.windowOrder.lastOrNull()?.app
        val scope = rememberCoroutineScope()

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
                    context = topApplication?.title.orEmpty()
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
            windowManager.Draw(
                modifier = Modifier
                    .fillMaxSize(),
                safeContentPadding = PaddingValues(0.dp)
            )
        }
    }
}

@Composable
private fun StatusBar(
    modifier: Modifier,
    context: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = .7f))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = context,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold
        )
    }
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
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        appList.forEach {
            it.Icon(
                modifier = Modifier.size(48.dp),
                onClick = {
                    onAppClicked.invoke(it)
                }
            )
        }
    }
}