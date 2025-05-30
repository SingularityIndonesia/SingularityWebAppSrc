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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import core.Shell
import kotlinx.coroutines.launch

@Composable
fun App() {
    val scope = rememberCoroutineScope()
    val shell = remember { Shell("main-shell") }

    DisposableEffect(shell) {

        val job = scope.launch {
//            shell.exec("/application/Console/index.html")
            shell.exec("/application/Desktop/index.html")
        }

        onDispose {
            job.cancel()
            scope.launch {
                shell.terminate()
                shell.destroy()
            }
        }
    }
}