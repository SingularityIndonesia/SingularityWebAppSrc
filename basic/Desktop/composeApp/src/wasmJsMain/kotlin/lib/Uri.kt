package lib

/**
 * Represents a URI reference to a file or resource
 */
interface Uri {
    /**
     * The name of the file or resource
     */
    val name: String

    /**
     * The size of the file in bytes
     */
    val size: Long

    /**
     * The MIME type of the file
     */
    val type: String

    /**
     * Reads the entire content of the file as a byte array
     */
    suspend fun readBytes(): ByteArray

    /**
     * Reads the entire content of the file as a string
     */
    suspend fun readText(): String
}