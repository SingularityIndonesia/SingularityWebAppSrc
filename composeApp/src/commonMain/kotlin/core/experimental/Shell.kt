package core.experimental

open class Process(
    open val id: String,
    open val command: String
) {
    open fun kill() {

    }
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class Shell(
    id: String
) {
    val processes: MutableList<Process>

    fun exec(url: String): Process
    fun kill(processId: String)
    fun terminate()
    fun destroy()
}
