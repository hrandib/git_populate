import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

val workingDir: File = File(System.getProperty("user.dir"))

fun String.asResourceStream(): InputStream? {
    val classloader = Thread.currentThread().contextClassLoader
    return classloader.getResourceAsStream(this)
}

fun commit(subj: String) {
    val command = "git commit -am"
    var cmdArgs = command.split(" ").toTypedArray() + subj
    ProcessBuilder(*cmdArgs)
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor(1, TimeUnit.SECONDS)
}

fun getLines(targetFile: File, amount: Int): Pair<List<String>, Int> {
    val targetEndLine = getLastLine(targetFile)
    val source = "names".asResourceStream()!!
    val namesList = BufferedReader(InputStreamReader(source)).readLines()
    var begin = namesList.indexOf(targetEndLine)
    if (begin == -1) {
        begin = 0
    } else {
        ++begin
    }
    val result = namesList.slice(begin until begin + amount)
    return Pair(result, begin)
}

private fun getLastLine(file: File): String {
    return file.readLines().last { it.isNotEmpty() }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println(
            """
            The test application that creates selected number of
            commits in the target file using sample strings
            from the source file

            Wrong arguments. Example:
                repop [file path] [commits amount]
            """.trimIndent()
        )
        exitProcess(1)
    }
    val commitCreateAmount: Int = if (args.size == 2) {
        args[1].toInt()
    } else 5
    val target = File(workingDir, args[0])
    val (lines, amount) = getLines(target, commitCreateAmount)
    for ((i, line) in lines.withIndex()) {
        target.appendText(line + "\n")
        commit("${i + amount}. $line")
    }
}
