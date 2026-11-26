package stat

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream

class ReportTest {

    private data class Output(val code: Int, val out: String, val err: String)

    private fun runCli(vararg args: String): Output {
        val oldOut = System.out
        val oldErr = System.err
        val out = ByteArrayOutputStream()
        val err = ByteArrayOutputStream()

        val code = try {
            System.setOut(PrintStream(out))
            System.setErr(PrintStream(err))
            run(arrayOf(*args))
        } finally {
            System.setOut(oldOut)
            System.setErr(oldErr)
        }

        return Output(code, out.toString(), err.toString())
    }

    private fun assertCliError(expectedError: String, vararg args: String) {
        val res = runCli(*args)
        assertEquals(1, res.code)
        assertEquals("", res.out)
        assertEquals(expectedError + System.lineSeparator(), res.err)
    }

    private fun File.createFile(relativePath: String, content: String): File =
        resolve(relativePath).apply {
            parentFile?.mkdirs()
            writeText(content)
        }

    @Test
    fun `prints report for valid dir`(@TempDir dir: File) {
        dir.createFile("Main.kt", "fun main() {\n    println(1)\n}")
        dir.createFile("README.md", "# Title")

        val res = runCli(dir.absolutePath)

        val expected = """
            Files sorted by extension:
            kt: 1 file, 3 lines
            md: 1 file, 1 line

            Top 3 longest files:
            Main.kt (3)
            README.md (1)
        """.trimIndent() + "\n\n"

        assertEquals(0, res.code)
        assertEquals("", res.err)
        assertEquals(expected, res.out)
    }

    @Test
    fun `prints blank line when no matching files`(@TempDir dir: File) {
        dir.createFile("notes.txt", "ignored")

        val res = runCli(dir.absolutePath)

        assertEquals(0, res.code)
        assertEquals("", res.err)
        assertEquals("\n", res.out)
    }

    @Test
    fun `fails without args`() {
        assertCliError("Error: expected exactly 1 argument (directory path), got 0")
    }

    @Test
    fun `fails with extra args`() {
        assertCliError("Error: expected exactly 1 argument (directory path), got 2", "dir1", "dir2")
    }

    @Test
    fun `fails when dir does not exist`(@TempDir dir: File) {
        val missing = dir.resolve("missing")
        assertCliError("Error: $missing is not a directory or doesn't exist", missing.absolutePath)
    }

    @Test
    fun `fails when path is a file`(@TempDir dir: File) {
        val file = dir.createFile("file.kt", "val x = 1")
        assertCliError("Error: $file is not a directory or doesn't exist", file.absolutePath)
    }

    @Test
    fun `formats plural and singular forms`() {
        val report = Report(
            sortedFiles = listOf(
                ExtensionStats("kt", 2, 15),
                ExtensionStats("md", 1, 1)
            ),
            longestFiles = listOf(
                FileStats("src/App.kt", 10),
                FileStats("src/Util.kt", 5),
                FileStats("README.md", 1)
            )
        )

        val expected = """
            Files sorted by extension:
            kt: 2 files, 15 lines
            md: 1 file, 1 line

            Top 3 longest files:
            src/App.kt (10)
            src/Util.kt (5)
            README.md (1)
        """.trimIndent() + "\n"

        assertEquals(expected, formatReport(report))
    }
}