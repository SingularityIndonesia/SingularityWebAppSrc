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
package lib

import androidx.compose.ui.graphics.Color
import lib.io.localstorage.LocalStorage

interface DesktopConfigPreference {
    fun getBackgroundColor(): Color?
    fun setBackgroundColor(color: Color)
}

class DesktopConfigPreferenceImpl(
    private val localStorage: LocalStorage
) : DesktopConfigPreference {
    companion object {
        private val BACKGROUND_COLOR_PREFERENCE_KEY = "${DesktopConfigPreference::class.simpleName}_background_color"
    }

    override fun getBackgroundColor(): Color? {
        // expecting format listOf(int R,int G,int B,int A)
        val colorHex = localStorage
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
        localStorage.set(
            BACKGROUND_COLOR_PREFERENCE_KEY,
            config
        )
    }
}