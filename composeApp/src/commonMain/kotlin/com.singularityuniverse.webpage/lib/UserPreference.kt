package com.singularityuniverse.webpage.lib

import androidx.compose.runtime.staticCompositionLocalOf
import com.singularityuniverse.webpage.lib.io.sharepreference.SharePreference
import com.singularityuniverse.webpage.lib.io.sharepreference.getSharePreference

interface UserPreference : DesktopConfigPreference

val LocalUserPreference = staticCompositionLocalOf<UserPreference> { UserPreferenceImpl(getSharePreference()) }

class UserPreferenceImpl(
    private val preference: SharePreference
) : UserPreference, DesktopConfigPreference by DesktopConfigPreferenceImpl(preference)