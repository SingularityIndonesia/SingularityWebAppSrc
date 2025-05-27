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

package org.singularityuniverse.console

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.*
import org.singularityuniverse.console.utils.jsEval
import org.singularityuniverse.console.utils.promptWrapper
import kotlin.js.Promise

class Console {
    val logs = mutableStateListOf<String>()
    val isProcessing = mutableStateOf(false)

    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    fun eval(prompt: String) {
        job?.cancel()

        isProcessing.value = true
        job = scope.launch {
            logs += "PROMPT: $prompt"

            // println("System: executing:\n$prompt")

            // Create a wrapper that captures console logs and handles all edge cases
            // fixme: wrapping it in promptWrapper causing every declared variable tobe local within the promptWrapper scope
            val wrappedPrompt = promptWrapper(prompt)
            // println("System: executing prompt as:\n$wrappedPrompt")

            val result = runCatching { jsEval(wrappedPrompt) }
                .onFailure { e -> logs += "Execution Error: $e" }
                .getOrNull() ?: return@launch

            // Safely handle the promise result
            val finalResult = runCatching {
                result.unsafeCast<Promise<JsAny>>().await<JsAny>()
            }.getOrElse { e -> "Promise Error: ${e.message}" }

            // Safely convert to string
            val resultString = runCatching {
                finalResult?.toString() ?: "null"
            }.getOrElse { e -> "Conversion Error: ${e.message}" }

            logs += resultString
        }

        job?.invokeOnCompletion {
            isProcessing.value = false
        }
    }
}
