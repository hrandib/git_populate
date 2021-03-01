import java.io.File
import java.io.FileInputStream
import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.util.concurrent.TimeUnit

fun String.asResourceUrl() : URL? {
    val classloader = Thread.currentThread().contextClassLoader
    return classloader.getResource("names")
}

val workingDir: File = File(System.getProperty("user.dir"))

fun String.runCommand(workingDir: File) {
    ProcessBuilder(*split(" ").toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor(60, TimeUnit.MINUTES)
}

fun commit(subj: String) {
    "git commit -am $subj".runCommand(workingDir)
}

fun getLines(sourcePath: String, targetPath: String) {

}

private fun getLastLine(filePath: String) : String {
    val fileInputStream = FileInputStream(filePath)
    val channel = fileInputStream.channel
    val buffer: ByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
    buffer.position(channel.size().toInt())
    var builder: StringBuilder? = StringBuilder()
    for (i in channel.size() - 1 downTo 0) {
        val c = buffer.get(i.toInt()).toChar()
        builder!!.append(c)
        if (c == '\n') {
            builder.reverse()
            break
        }
    }
    return builder.toString()
}

fun main(args: Array<String>) {
    val commitCreateAmount: Int = if (args.isNotEmpty()) {
        args[0].toInt()
    } else 5
    println(getLastLine("names".asResourceUrl().path))
}
