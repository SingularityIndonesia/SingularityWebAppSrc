package com.singularityuniverse.webpage.application

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.singularityuniverse.webpage.core.Application
import com.singularityuniverse.webpage.core.Window
import com.singularityuniverse.webpage.core.WindowManager

class Desktop : Application() {
    override val title: String = "Desktop"
    override val defaultMinSize: DpSize = DpSize.Unspecified

    @Composable
    override fun Draw(modifier: Modifier) {
        val windowManager = remember { WindowManager() }
        val applications = remember {
            mutableStateListOf<Application>(
                About()
            )
        }
        val windows = remember { mutableStateListOf<Window>() }

        LaunchedEffect(Unit) {
            // open about on init
            windows += windowManager.requestWindow(applications.first())
            windowManager.open(windows.first { it.app is About })
        }

        windowManager.Draw(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Blue.copy(alpha = .7f))
        )
    }
}


