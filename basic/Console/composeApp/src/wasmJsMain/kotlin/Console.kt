package org.singularityuniverse.console

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlin.js.Promise

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
            val actualPrompt = prompt.split(">").last().trim()
            
            try {
                // Create a wrapper that captures console logs and handles all edge cases
                val wrappedPrompt = """
                    (async function() {
                        // Array to capture all console outputs
                        let capturedLogs = [];
                        
                        // Store original console methods
                        const originalConsole = {
                            log: console.log,
                            error: console.error,
                            warn: console.warn,
                            info: console.info,
                            debug: console.debug
                        };
                        
                        // Override console methods to capture output
                        console.log = (...args) => {
                            capturedLogs.push('LOG: ' + args.map(arg => 
                                typeof arg === 'object' ? JSON.stringify(arg, null, 2) : String(arg)
                            ).join(' '));
                            originalConsole.log(...args);
                        };
                        
                        console.error = (...args) => {
                            capturedLogs.push('ERROR: ' + args.map(arg => 
                                typeof arg === 'object' ? JSON.stringify(arg, null, 2) : String(arg)
                            ).join(' '));
                            originalConsole.error(...args);
                        };
                        
                        console.warn = (...args) => {
                            capturedLogs.push('WARN: ' + args.map(arg => 
                                typeof arg === 'object' ? JSON.stringify(arg, null, 2) : String(arg)
                            ).join(' '));
                            originalConsole.warn(...args);
                        };
                        
                        console.info = (...args) => {
                            capturedLogs.push('INFO: ' + args.map(arg => 
                                typeof arg === 'object' ? JSON.stringify(arg, null, 2) : String(arg)
                            ).join(' '));
                            originalConsole.info(...args);
                        };
                        
                        console.debug = (...args) => {
                            capturedLogs.push('DEBUG: ' + args.map(arg => 
                                typeof arg === 'object' ? JSON.stringify(arg, null, 2) : String(arg)
                            ).join(' '));
                            originalConsole.debug(...args);
                        };
                        
                        let result;
                        let errorOccurred = false;
                        let errorMessage = '';
                        
                        try {
                            result = ${actualPrompt.removeSuffix(";")};
                            
                            // Handle promises
                            if (result && typeof result.then === 'function') {
                                result = await result;
                            }
                            
                        } catch (error) {
                            errorOccurred = true;
                            errorMessage = 'JavaScript Error: ' + error.message;
                            capturedLogs.push('ERROR: ' + error.message);
                        } finally {
                            // Restore original console methods
                            console.log = originalConsole.log;
                            console.error = originalConsole.error;
                            console.warn = originalConsole.warn;
                            console.info = originalConsole.info;
                            console.debug = originalConsole.debug;
                        }
                        
                        // Format the final output
                        let output = [];
                        
                        // Add all captured logs
                        if (capturedLogs.length > 0) {
                            output = output.concat(capturedLogs);
                        }
                        
                        // Add result or error
                        if (errorOccurred) {
                            if (!capturedLogs.some(log => log.includes(errorMessage.replace('JavaScript Error: ', '')))) {
                                output.push(errorMessage);
                            }
                        } else {
                            // Format the result
                            let resultString;
                            if (result === null) {
                                resultString = "null";
                            } else if (result === undefined) {
                                resultString = "undefined";
                            } else if (typeof result === 'function') {
                                resultString = result.toString();
                            } else if (typeof result === 'object') {
                                try {
                                    resultString = JSON.stringify(result, null, 2);
                                } catch (e) {
                                    resultString = result.toString();
                                }
                            } else {
                                resultString = String(result);
                            }
                            
                            // Only add result if it's not empty and not just whitespace
                            if (resultString.trim() !== '') {
                                output.push("RESULT: " + resultString);
                            }
                        }
                        
                        // Join all output with newlines, or return a default message if empty
                        return output.length > 0 ? output.join('\n') : 'No output';
                    })()
                """.trimIndent()
                
                val result = jsEval(wrappedPrompt)
                
                // Safely handle the promise result
                val finalResult = try {
                    result.unsafeCast<Promise<JsAny>>().await<JsAny>()
                } catch (e: Exception) {
                    "Promise Error: ${e.message}"
                }
                
                // Safely convert to string
                val resultString = try {
                    finalResult?.toString() ?: "null"
                } catch (e: Exception) {
                    "Conversion Error: ${e.message}"
                }
                
                logs += resultString
                
            } catch (e: Exception) {
                logs += "Evaluation Error: ${e.message ?: "Unknown error"}"
            }
            
            isProcessing.value = false
        }
    }
}
