package stat

import java.io.File
import java.nio.file.Files

private val ALLOWED = setOf("kt", "kts", "md")
private val IGNORED = setOf(".git", ".gradle", "build")
const val LIMIT = 3

data class ExtensionStats(
    val extension: String,
    val fileCount: Int,
    val lineCount: Int
)

private data class FileInfo(
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

private fun formStats(path: File): List<FileInfo> {
    require(path.isDirectory) { "Error: $path is not a directory or doesn't exist" }
    val allFiles = path.walkTopDown()
        .onEnter { dir -> dir.name !in IGNORED && !Files.isSymbolicLink(dir.toPath()) }
        .filter { file -> file.isFile && file.extension.lowercase() in ALLOWED && !Files.isSymbolicLink(file.toPath()) }
    return allFiles.map { file ->
        val lineCount = file.useLines { it.count() }
        val relativePath = file.relativeTo(path).invariantSeparatorsPath
        val extension = file.extension.lowercase()
        FileInfo(relativePath, lineCount, extension)
    }
        .toList()
}

fun analyzePath(path: File): Report {
    val stats = formStats(path)
    return Report(
        sortByExt(stats),
        sortBySize(stats)
    )
}

private fun sortByExt(stats: List<FileInfo>): List<ExtensionStats> {
    return stats
        .groupBy { it.extension }
        .map { (ext, group) -> ExtensionStats(ext, group.size, group.sumOf { it.lineCount }) }
        .sortedBy { it.extension }
}

private fun sortBySize(stats: List<FileInfo>): List<FileStats> {
    return stats.sortedWith(compareByDescending<FileInfo> { it.lineCount }.thenBy { it.relativePath })
        .take(LIMIT)
        .map { FileStats(it.relativePath, it.lineCount) }
}