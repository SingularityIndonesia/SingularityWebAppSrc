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

class Desktop : Application() {
    override val title: String = "Desktop"
    override val defaultMinSize: DpSize = DpSize.Unspecified
    private val windowManager = WindowManager()
    private val applications = mutableStateListOf<Application>(About())
    private val windows = mutableStateListOf<Window>()

    @Composable
    override fun Draw(modifier: Modifier) {
        val topApplication = windowManager.windowOrder.lastOrNull()?.app

        LaunchedEffect(Unit) {
            // open about on init
            windows += windowManager.requestWindow(applications.first())
            windowManager.open(windows.first { it.app is About })
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


