package stat

import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    exitProcess(run(args))
}

internal fun run(args: Array<String>): Int {
    if (args.size != 1) {
        System.err.println("Error: expected exactly 1 argument (directory path), got ${args.size}")
        return 1
    }
    val path = File(args[0])
    val report = try {
        analyzePath(path)
    } catch (e: IllegalArgumentException) {
        System.err.println(e.message)
        return 1
    }
    println(formatReport(report))
    return 0
}

internal fun formatReport(report: Report): String = buildString {
    if (report.sortedFiles.isNotEmpty()) {
        appendLine("Files sorted by extension:")
        for ((extension, fileCount, lineCount) in report.sortedFiles) {
            appendLine(
                "$extension: $fileCount file${if (fileCount != 1) "s" else ""}, " +
                        "$lineCount line${if (lineCount != 1) "s" else ""}"
            )
        }
    }
    if (report.longestFiles.isNotEmpty()) {
        if (isNotEmpty()) appendLine()
        appendLine("Top $LIMIT longest files:")
        for ((path, count) in report.longestFiles) {
            appendLine("$path ($count)")
        }
    }
}