package com.singularityuniverse.webpage.lib.io.localstorage

interface LocalStorage {
    fun set(key: String, data: String)
    fun get(key: String): String
}

expect fun getLocalStorage(): LocalStorage