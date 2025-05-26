package org.singularityuniverse.console

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@JsName("eval")
external fun jsEval(arg: String): JsAny

val windowId = window.frameElement?.id

class Console {
    val logs = mutableStateListOf<String>()
    val isProcessing = mutableStateOf(false)

    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    /**
     * @param prompt semantic = prefix > actual_prompt
     */
    fun eval(prompt: String) {
        job?.cancel()

        isProcessing.value = true
        job = scope.launch {
            logs += prompt
            val result = runCatching { jsEval(prompt.split(">").last()) }
                .getOrElse { it.toString() }
            logs += result.toString()
            isProcessing.value = false
        }
    }
}