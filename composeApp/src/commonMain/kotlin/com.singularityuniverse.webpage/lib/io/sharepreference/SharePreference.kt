package com.singularityuniverse.webpage.lib.io.sharepreference

interface SharePreference {
    fun set(key: String, data: String)
    fun get(key: String): String
}