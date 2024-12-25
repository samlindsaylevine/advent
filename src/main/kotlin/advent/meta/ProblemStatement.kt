package advent.meta

import org.jsoup.Jsoup
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDate

class ProblemStatement(val original: String) {

    companion object {
        fun retrieve(client: HttpClient, date: LocalDate = LocalDate.now()): ProblemStatement {
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://adventofcode.com/${date.year}/day/${date.dayOfMonth}"))
                .header("User-Agent", AdventOfCodeUserAgent.HEADER)
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            // We'll stick an extra newline in after the headers - without this, jsoup runs it together with the next line.
            val massagedBody = response.body().replace("</h2><p>", "</h2><p>\n")

            val document = Jsoup.parse(massagedBody)

            val articles = document.getElementsByClass("day-desc")

            return ProblemStatement(articles.joinToString("\n") { it.wholeText() })
        }
    }

    /**
     * Takes a problem statement, as from the website, and converts it into a format of a Javadoc comment suitable for
     * including in source code.
     *
     * This means:
     * a) It has a leading /** and trailing */
     * b) Each line starts with a * at the appropriate indentation level
     * c) Lines are line-breaked at the appropriate width
     */
    fun toJavadoc(): String {
        // Make sure everything fits within 120, including the leading " * " in the comment.
        val maxWidth = 120 - 3

        val docBody = original.lines()
            .flatMap { splitLine(it, maxWidth) }
            .joinToString("\n") { " * $it" }

        return "/**\n" +
                docBody + "\n" +
                " */"
    }

}

private fun splitLine(line: String, maxWidth: Int): List<String> {
    if (line.length <= maxWidth) return listOf(line)

    val splitIndex = line.take(maxWidth).lastIndexOf(" ")

    return if (splitIndex == -1) {
        // Oops, there isn't any whitespace before the maximum width at all! Is there any at all?
        val firstWhitespace = line.indexOf(" ")
        return if (firstWhitespace == -1) {
            // No whitespace at all! Just return the line.
            listOf(line)
        } else {
            listOf(line.take(firstWhitespace)) + splitLine(line.substring(firstWhitespace + 1), maxWidth)
        }
    } else {
        listOf(line.take(splitIndex)) + splitLine(line.substring(splitIndex + 1), maxWidth)
    }
}

private fun String.copyToClipboard() {
    System.setProperty("java.awt.headless", "false")
    val selection = StringSelection(this)
    Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, null)
}

/**
 * Download today's problem statement, format it to put it as a Javadoc comment, and copy it to the clipboard.
 *
 * If you want to do some other day besides today, provide it as an argument to "retrieve".
 *
 * Best run after solving the day's problem so that both parts are visible.
 */
fun main() {
    val statement = ProblemStatement.retrieve(AdventOfCodeCookies.clientWithCookies())

    val javadoc = statement.toJavadoc()
    javadoc.copyToClipboard()
    println("Statement copied to clipboard!")
}