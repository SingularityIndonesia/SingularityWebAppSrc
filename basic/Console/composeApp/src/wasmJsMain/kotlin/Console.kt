package org.singularityuniverse.console

// External JavaScript eval function that returns a string
external fun jsEval(code: String): String

// JavaScript execution function
fun executeJavaScript(code: String): String {
    return try {
        val result = jsEval("""
            (function() {
                // Capture console output
                var capturedOutput = [];
                var originalConsole = {
                    log: console.log,
                    warn: console.warn,
                    error: console.error,
                    info: console.info
                };
                
                // Override console methods to capture output
                console.log = function(...args) {
                    capturedOutput.push(args.map(arg => 
                        typeof arg === 'object' ? JSON.stringify(arg) : String(arg)
                    ).join(' '));
                    originalConsole.log.apply(console, args);
                };
                
                console.warn = function(...args) {
                    capturedOutput.push('WARN: ' + args.map(arg => 
                        typeof arg === 'object' ? JSON.stringify(arg) : String(arg)
                    ).join(' '));
                    originalConsole.warn.apply(console, args);
                };
                
                console.error = function(...args) {
                    capturedOutput.push('ERROR: ' + args.map(arg => 
                        typeof arg === 'object' ? JSON.stringify(arg) : String(arg)
                    ).join(' '));
                    originalConsole.error.apply(console, args);
                };
                
                console.info = function(...args) {
                    capturedOutput.push('INFO: ' + args.map(arg => 
                        typeof arg === 'object' ? JSON.stringify(arg) : String(arg)
                    ).join(' '));
                    originalConsole.info.apply(console, args);
                };
                
                try {
                    // Execute the code
                    var result = eval(`${code.replace("`", "\\`").replace("\\", "\\\\")}`);
                    
                    // Restore original console methods
                    console.log = originalConsole.log;
                    console.warn = originalConsole.warn;
                    console.error = originalConsole.error;
                    console.info = originalConsole.info;
                    
                    // If there was console output, return that
                    if (capturedOutput.length > 0) {
                        var output = capturedOutput.join('\n');
                        // If the result is also meaningful (not undefined), include it
                        if (result !== undefined && result !== null) {
                            return output + '\n→ ' + (typeof result === 'object' ? JSON.stringify(result) : String(result));
                        }
                        return output;
                    }
                    
                    // No console output, return the result
                    if (result === null) return "null";
                    if (result === undefined) return "undefined";
                    if (typeof result === 'string') return result;
                    if (typeof result === 'number') return result.toString();
                    if (typeof result === 'boolean') return result.toString();
                    if (typeof result === 'function') return result.toString();
                    if (typeof result === 'object') return JSON.stringify(result, null, 2);
                    return String(result);
                    
                } catch(e) {
                    // Restore original console methods
                    console.log = originalConsole.log;
                    console.warn = originalConsole.warn;
                    console.error = originalConsole.error;
                    console.info = originalConsole.info;
                    
                    return "Error: " + e.message;
                }
            })()
        """.trimIndent())
        
        result
    } catch (e: Throwable) {
        "Error: ${e.message ?: "Unknown error"}"
    }
}