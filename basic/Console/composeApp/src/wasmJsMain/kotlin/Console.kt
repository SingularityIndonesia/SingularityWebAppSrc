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
            isProcessing.value = false
        }
    }
}
