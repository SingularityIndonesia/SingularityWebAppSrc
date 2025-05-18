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
package lib.io.localstorage

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