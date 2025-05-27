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

package core

import kotlinx.browser.document
import org.w3c.dom.HTMLIFrameElement
import org.w3c.dom.Window

@JsFun(code = "(w, name, script) => eval(`w." + "$" + "{name} = " + "$" + "{script}`)")
external fun injectFunction(w: Window, name: String, script: String)

@JsFun(code = "(name, callback) => window[name] = callback")
external fun setWindowCallback(name: String, callback: (String) -> Unit)

open class Process(
    open val id: String,
    open val command: String
) {
    open fun kill() {

    }
}

class Shell(
    override val id: String,
) : Process(id, "/") {
    val processes = mutableListOf<Process>()

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
    fun exec(url: String): Process {
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
        return proc
    }

    private fun injectBasicUtilsToIframe(iframe: HTMLIFrameElement) {
        iframe.onload = {
            // region inject exec
            val execCallback: (String) -> Unit = { url ->
                println("trying to execute $url")
                this@Shell.exec(url)
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

            injectFunction(
                iframe.contentWindow!!.window,
                "help",
                "() => fetch('/docs/help.txt').then(r => r.text())"
            )

            injectFunction(
                iframe.contentWindow!!.window,
                "info",
                "() => fetch('/docs/info.txt').then(r => r.text())"
            )
        }
    }

    fun kill(processId: String) {
        // remove iframe, and destroy instance
        val proc = processes.find { it.id == processId } ?: return

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

    fun terminate() {
        processes.forEach { kill(it.id) }
    }

    fun destroy() {

    }
}

