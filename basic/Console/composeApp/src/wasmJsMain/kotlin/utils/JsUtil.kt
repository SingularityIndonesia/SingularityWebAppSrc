package org.singularityuniverse.console.utils

@JsName("eval")
external fun jsEval(arg: String): JsAny

fun promptWrapper(prompt: String): String =
    """
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
                capturedLogs.push(args.map(arg => 
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
            
            // Function to restore console methods
            const restoreConsole = () => {
                console.log = originalConsole.log;
                console.error = originalConsole.error;
                console.warn = originalConsole.warn;
                console.info = originalConsole.info;
                console.debug = originalConsole.debug;
            };
            
            let result;
            let errorOccurred = false;
            let errorMessage = '';
            
            try {
                result = ${prompt.removeSuffix(";")};
                
                // Handle promises - wait for them to complete before restoring console
                if (result && typeof result.then === 'function') {
                    // Wait a bit more to ensure all promise chains and console logs are captured
                    result = await Promise.race([
                        result,
                        new Promise((_, reject) => 
                            setTimeout(() => reject(new Error('Promise timeout after 10 seconds')), 10000)
                        )
                    ]);
                    
                    // Give a small delay to ensure any remaining console logs in promise chains are captured
                    await new Promise(resolve => setTimeout(resolve, 100));
                }
                
            } catch (error) {
                errorOccurred = true;
                errorMessage = 'JavaScript Error: ' + error.message;
                capturedLogs.push('ERROR: ' + error.message);
            } finally {
                // Restore console methods after all promises are resolved
                restoreConsole();
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
                    output.push(resultString);
                }
            }
            
            // Join all output with newlines, or return a default message if empty
            return output.length > 0 ? output.join('\n') : 'No output';
        })()
    """.trimIndent()