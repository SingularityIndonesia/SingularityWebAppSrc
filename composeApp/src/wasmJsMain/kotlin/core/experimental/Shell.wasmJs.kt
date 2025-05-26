package core.experimental

import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLIFrameElement
import org.w3c.dom.Window

@JsFun(code = "(w, name, script) => eval(`w." + "$" + "{name} = " + "$" + "{script}`)")
external fun injectFunction(w: Window, name: String, script: String)

@JsFun(code = "(name, callback) => window[name] = callback")
external fun setWindowCallback(name: String, callback: (String) -> Unit)

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Shell actual constructor(
    override val id: String,
) : Process(id, "/") {

    private val scope = CoroutineScope(Dispatchers.Main)
    actual val processes = mutableListOf<Process>()

    private val nextId: String
        get() = processes
            .maxOfOrNull {
                // semantic: proc-n
                it.id
                    .split("-")
                    .last().toInt()
            }
            ?.let { "${id}/proc-${it + 1}" }
            ?: "${id}/proc-0"

    /** launch url within an iframe.
     * ```
     * <div id="root">
     *     <iframe id="proc-0"/>
     * </div>
     * ```
     */
    actual suspend fun exec(url: String): Process = coroutineScope {
        val proc = Process(id = nextId, command = url)
        val mainContainer = document.getElementById("root")
            ?: throw NullPointerException("Error: no root element found")

        with(mainContainer) {
            val iframe = document.createElement("iframe") as HTMLIFrameElement
            with(iframe) {
                id = proc.id
                src = proc.command
                style.width = "100%"
                style.height = "100%"
                style.border = "none"
                style.top = "0"
                style.left = "0"
                style.position = "absolute"

                injectBasicUtilsToIframe(this)
            }
            appendChild(iframe)
        }

        processes.add(proc)
        println("Process ${proc.id} finished")
        proc
    }

    private fun injectBasicUtilsToIframe(iframe: HTMLIFrameElement) {
        iframe.onload = {
            // region inject exec
            val execCallback: (String) -> Unit = { url ->
                println("trying to execute $url")

                scope.launch {
                    this@Shell.exec(url)
                }
            }

            // Store callback on parent window with unique name
            val callbackName = "shellExec_${this@Shell.id.replace("/", "_").replace("-", "_")}"

            // Store the callback function in the parent window
            setWindowCallback(callbackName, execCallback)

            injectFunction(
                iframe.contentWindow!!.window,
                "exec",
                "(url) => window.parent.$callbackName(url)"
            )
            // endregion

            injectFunction(
                iframe.contentWindow!!.window,
                "shellId",
                "`${this@Shell.id}`"
            )

            injectFunction(
                iframe.contentWindow!!.window,
                "procId",
                "`${iframe.id}`"
            )
        }
    }

    actual suspend fun kill(processId: String) {
        // remove iframe, and destroy instance
        scope.launch {
            val proc = processes.find { it.id == processId } ?: return@launch

            proc.kill()
            processes.remove(proc)
            val iframe = document.getElementById(proc.id)
            if (iframe != null) {
                iframe.remove()
                println("Removed iframe: ${proc.id}")
            } else {
                println("Could not find iframe with id: ${proc.id}")
            }
        }
    }

    actual suspend fun terminate() {
        processes.forEach { kill(it.id) }
    }

    actual suspend fun destroy() {

    }
}

