package lib.os

import kotlinx.coroutines.suspendCancellableCoroutine
import lib.Uri
import org.w3c.dom.HTMLInputElement
import org.w3c.files.File
import kotlinx.browser.document
import kotlinx.coroutines.CancellationException
import kotlin.coroutines.resume

/**
 * Exceptions
 */
private val UserCancelException = CancellationException("user cancel the action")
private val UnknownNPE = NullPointerException("unknown NPE")

/**
 * WASM/JS implementation of file picker
 */
actual suspend fun pickFile(): Result<Uri> = suspendCancellableCoroutine { continuation ->
    try {
        // Create a hidden file input element
        val input = document.createElement("input") as HTMLInputElement
        input.type = "file"
        input.style.display = "none"
        
        // Add event listener for file selection
        input.addEventListener("change") { _ ->
            val files = input.files
            if (files != null && files.length > 0) {
                val file = files.item(0)
                if (file != null) {
                    val uri = WebUri(file)
                    continuation.resume(Result.success(uri))
                } else {
                    continuation.resume(Result.failure(UnknownNPE))
                }
            } else {
                // User cancelled the dialog
                continuation.resume(Result.failure(UserCancelException))
            }
            
            // Clean up - remove the input element
            input.remove()
        }
        
        // Add event listener for dialog cancellation
        input.addEventListener("cancel") {
            continuation.resume(Result.failure(UserCancelException))
            input.remove()
        }
        
        // Add the input to the document and trigger the file dialog
        document.body?.appendChild(input)
        input.click()
        
        // Set up cancellation handler
        continuation.invokeOnCancellation {
            input.remove()
        }
        
    } catch (e: Exception) {
        continuation.resume(Result.failure(e))
    }
}

/**
 * Web implementation of Uri interface using browser File API
 */
private class WebUri(private val file: File) : Uri {
    override val name: String get() = file.name
    override val size: Long get() = file.size.toDouble().toLong()
    override val type: String get() = file.type
    
    override suspend fun readBytes(): ByteArray = suspendCancellableCoroutine { continuation ->
        val reader = org.w3c.files.FileReader()
        
        reader.onload = {
            try {
                val result = reader.result
                if (result is org.khronos.webgl.ArrayBuffer) {
                    // Convert ArrayBuffer to ByteArray using DataView
                    val dataView = org.khronos.webgl.DataView(result)
                    val byteArray = ByteArray(result.byteLength) { i ->
                        dataView.getUint8(i)
                    }
                    continuation.resume(byteArray)
                } else {
                    continuation.resume(ByteArray(0))
                }
            } catch (_: Exception) {
                continuation.resume(ByteArray(0))
            }
        }
        
        reader.onerror = {
            continuation.resume(ByteArray(0))
        }
        
        continuation.invokeOnCancellation {
            reader.abort()
        }
        
        reader.readAsArrayBuffer(file)
    }
    
    override suspend fun readText(): String = suspendCancellableCoroutine { continuation ->
        val reader = org.w3c.files.FileReader()
        
        reader.onload = {
            val result = reader.result
            continuation.resume(result?.toString() ?: "")
        }
        
        reader.onerror = {
            continuation.resume("")
        }
        
        continuation.invokeOnCancellation {
            reader.abort()
        }
        
        reader.readAsText(file)
    }
}
