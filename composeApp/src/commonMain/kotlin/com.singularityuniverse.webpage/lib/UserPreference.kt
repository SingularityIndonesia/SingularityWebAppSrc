package com.singularityuniverse.webpage.lib

import androidx.compose.runtime.staticCompositionLocalOf
import com.singularityuniverse.webpage.lib.io.localstorage.LocalStorage
import com.singularityuniverse.webpage.lib.io.localstorage.getLocalStorage

interface UserPreference : DesktopConfigPreference

val LocalUserPreference = staticCompositionLocalOf<UserPreference> { UserPreferenceImpl(getLocalStorage()) }

class UserPreferenceImpl(
    private val localStorage: LocalStorage
) : UserPreference, DesktopConfigPreference by DesktopConfigPreferenceImpl(localStorage)