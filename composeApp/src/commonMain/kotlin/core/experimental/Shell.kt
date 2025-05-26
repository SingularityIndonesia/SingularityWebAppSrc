package core.experimental

open class Process(
    open val id: String,
    open val command: String
) {
    open suspend fun kill() {

    }
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class Shell(
    id: String
) {
    val processes: MutableList<Process>

    suspend fun exec(url: String): Process
    suspend fun kill(processId: String)
    suspend fun terminate()
    suspend fun destroy()
}
