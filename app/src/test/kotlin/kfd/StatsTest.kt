package kfd

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.api.BeforeEach
import java.io.File
import java.nio.file.Path

class StatsTest {

    @TempDir
    lateinit var testDir: File

    @BeforeEach
    fun setUp() {
        val srcDir = File(testDir, "src").apply { mkdirs() }
        File(srcDir, "A.kt").writeText("package sample\nclass A")
        File(srcDir, "B.kt").writeText("package sample\nclass B\n")
        File(testDir, "README.md").writeText("")
        File(testDir, "notes.txt").writeText("ignored")
    }

    @Test
    fun `first report counts files and lines by extension`() {
        val report = analyzePath(testDir)

        val expected = listOf(
            ExtensionStats(extension = "kt", fileCount = 2, lineCount = 4),
            ExtensionStats(extension = "md", fileCount = 1, lineCount = 0)
        )
        assertEquals(expected, report.sortedFiles)
    }

    @Test
    fun `second report sorts longest files by lines desc and path asc`() {
        val report = analyzePath(testDir)

        val expected = listOf(
            FileStats(relativePath = "src/A.kt", lineCount = 2),
            FileStats(relativePath = "src/B.kt", lineCount = 2),
            FileStats(relativePath = "README.md", lineCount = 0)
        )
        assertEquals(expected, report.longestFiles)
    }

    @Test
    fun `formatReport produces expected text output`() {
        val report = analyzePath(testDir)
        val formatted = formatReport(report)

        val expectedText = buildString {
            appendLine("Files by extension:")
            appendLine("kt: 2 files, 4 lines")
            appendLine("md: 1 file, 0 lines")
            appendLine()
            appendLine("Top 3 longest files:")
            appendLine("src/A.kt (2)")
            appendLine("src/B.kt (2)")
            appendLine("README.md (0)")
        }

        assertEquals(expectedText, formatted)
    }

    @Test
    fun `empty directory returns empty reports`(@TempDir emptyDir: Path) {
        val report = analyzePath(emptyDir.toFile())

        assertTrue(report.sortedFiles.isEmpty())
        assertTrue(report.longestFiles.isEmpty())
        assertEquals("", formatReport(report))
    }

    @Test
    fun `invalid input path throws IllegalArgumentException and run returns non-zero code`() {
        val nonExistent = File(testDir, "non_existent_folder")
        val regularFile = File(testDir, "README.md")

        assertThrows(IllegalArgumentException::class.java) {
            analyzePath(nonExistent)
        }
        assertThrows(IllegalArgumentException::class.java) {
            analyzePath(regularFile)
        }

        assertEquals(1, run(emptyArray()))
        assertEquals(1, run(arrayOf(testDir.path, "extra_arg")))
        assertEquals(1, run(arrayOf(nonExistent.path)))
        assertEquals(1, run(arrayOf(regularFile.path)))
    }

}
