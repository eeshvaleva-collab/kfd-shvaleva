package stat

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.io.File

class StatsTest {

    private fun File.createFile(relativePath: String, content: String = ""): File =
        resolve(relativePath).apply {
            parentFile?.mkdirs()
            writeText(content)
        }

    @Test
    fun `counts files and lines by extension`(@TempDir dir: File) {
        dir.createFile("src/A.kt", "package sample\nclass A")
        dir.createFile("src/B.kt", "package sample\nclass B\n")
        dir.createFile("README.md")
        dir.createFile("notes.txt", "ignored")
        val report = analyzePath(dir)

        val expected = listOf(
            ExtensionStats("kt", 2, 4),
            ExtensionStats("md", 1, 0)
        )
        assertEquals(expected, report.sortedFiles)
    }

    @Test
    fun `sorts longest files by lines desc and path asc`(@TempDir dir: File) {
        dir.createFile("src/A.kt", "package sample\nclass A")
        dir.createFile("src/B.kt", "package sample\nclass B\n")
        dir.createFile("README.md")
        dir.createFile("notes.txt", "ignored")
        val report = analyzePath(dir)

        val expected = listOf(
            FileStats("src/A.kt", 2),
            FileStats("src/B.kt", 2),
            FileStats("README.md", 0)
        )
        assertEquals(expected, report.longestFiles)
    }

    @Test
    fun `returns empty report for empty directory`(@TempDir dir: File) {
        val report = analyzePath(dir)

        assertTrue(report.sortedFiles.isEmpty())
        assertTrue(report.longestFiles.isEmpty())
    }

    @Test
    fun `fails when directory does not exist`(@TempDir dir: File) {
        val missing = dir.resolve("missing")

        val ex = assertThrows<IllegalArgumentException> {
            analyzePath(missing)
        }
        assertEquals("Error: $missing is not a directory or doesn't exist", ex.message)
    }
}
