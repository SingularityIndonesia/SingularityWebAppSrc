package com.singularityuniverse.webpage.lib.io.sharepreference

actual fun getSharePreference(): SharePreference {
    return WasmJsSharePreference()
}