package com.singularityuniverse.webpage.lib.io.localstorage

import kotlinx.browser.localStorage

actual fun getLocalStorage(): LocalStorage {
    return WasmJsLocalStorage()
}

/**
 * WasmJS implementation of SharePreference using the browser's localStorage
 * for persistent data storage in the web environment.
 */
class WasmJsLocalStorage : LocalStorage {
    /**
     * Stores a string value associated with the given key in localStorage.
     *
     * @param key The key to associate with the data
     * @param data The string data to store
     */
    override fun set(key: String, data: String) {
        localStorage.setItem(key, data)
    }

    /**
     * Retrieves the string value associated with the given key from localStorage.
     *
     * @param key The key to look up
     * @return The stored string value, or an empty string if the key doesn't exist
     */
    override fun get(key: String): String {
        return localStorage.getItem(key) ?: ""
    }
}