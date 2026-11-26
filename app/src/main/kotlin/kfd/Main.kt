package kfd
import kotlin.system.exitProcess
import java.io.File
import java.nio.file.Files

private val ALLOWED = setOf("kt", "kts", "md")
private val IGNORED = setOf(".git", ".gradle", "build")
private const val LIMIT = 3
data class ExtensionStats(
    val extension: String,
    val fileCount: Int,
    val lineCount: Int
)

data class FileInfo(
    val relativePath: String,
    val lineCount: Int,
    val extension: String
)
data class FileStats(
    val relativePath: String,
    val lineCount: Int
)

data class Report(
    val sortedFiles: List<ExtensionStats>,
    val longestFiles: List<FileStats>
)

fun main(args: Array<String>){
    exitProcess(run(args))
}

fun run(args: Array<String>): Int{
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

fun formStats(path: File): Sequence<FileInfo> {
    require(path.isDirectory){ "Error: $path is not a directory or doesn't exist"}
    val allFiles = path.walkTopDown()
        .onEnter { dir -> dir.name !in IGNORED && !Files.isSymbolicLink(dir.toPath())}
        .filter {file -> file.isFile && file.extension in ALLOWED && !Files.isSymbolicLink(file.toPath())}
    return allFiles.map { file ->
        val lineCount = file.useLines { it.count() }
        val relativePath = file.relativeTo(path).invariantSeparatorsPath
        val extension = file.extension
        FileInfo(relativePath, lineCount, extension)
    }
}

fun formatReport(report: Report): String = buildString {
    if (report.sortedFiles.isNotEmpty()) {
        appendLine("Files by extension:")
        for ((extension, fileCount, lineCount) in report.sortedFiles) {
            appendLine(
                "$extension: $fileCount file${if (fileCount != 1) "s" else ""}, " +
                        "$lineCount line${if (lineCount != 1) "s" else ""}"
            )
        }
    }
    if (report.longestFiles.isNotEmpty()) {
        if (isNotEmpty()) appendLine()
        appendLine("Top 3 longest files:")
        for ((path, count) in report.longestFiles) {
            appendLine("$path ($count)")
        }
    }
}

fun analyzePath(path: File): Report {
    val stats = formStats(path).toList()
    return Report(
        sortByExt(stats),
        sortBySize(stats))
}

fun sortByExt(stats: List<FileInfo>): List<ExtensionStats> {
    return stats
        .groupBy { it.extension }
        .map {(ext, group) -> ExtensionStats(ext, group.size, group.sumOf { it.lineCount })}
        .sortedBy { it.extension }
}

fun sortBySize(stats: List<FileInfo>): List<FileStats> {
    return stats.sortedWith (compareByDescending<FileInfo> { it.lineCount }.thenBy { it.relativePath })
        .take(LIMIT)
        .map { FileStats(it.relativePath, it.lineCount) }
        .toList()
}