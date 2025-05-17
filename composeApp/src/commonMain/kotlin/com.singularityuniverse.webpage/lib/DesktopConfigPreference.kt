package com.singularityuniverse.webpage.lib

import androidx.compose.ui.graphics.Color
import com.singularityuniverse.webpage.lib.io.sharepreference.SharePreference

interface DesktopConfigPreference {
    fun getBackgroundColor(): Color?
    fun setBackgroundColor(color: Color)
}

class DesktopConfigPreferenceImpl(
    private val sharedPreference: SharePreference
) : DesktopConfigPreference {
    companion object {
        private val BACKGROUND_COLOR_PREFERENCE_KEY = "${DesktopConfigPreference::class.simpleName}_background_color"
    }

    override fun getBackgroundColor(): Color? {
        // expecting format listOf(int R,int G,int B,int A)
        val colorHex = sharedPreference
            .get(BACKGROUND_COLOR_PREFERENCE_KEY)
            .split(",")
            .map { it.toInt() }
        return runCatching {
            Color(
                red = colorHex[0],
                green = colorHex[1],
                blue = colorHex[2],
                alpha = colorHex[3]
            )
        }.getOrNull()
    }

    override fun setBackgroundColor(color: Color) {
        val config = color.run { listOf(red, green, blue, alpha) }.joinToString(",") { it.toInt().toString() }
        sharedPreference.set(
            BACKGROUND_COLOR_PREFERENCE_KEY,
            config
        )
    }
}