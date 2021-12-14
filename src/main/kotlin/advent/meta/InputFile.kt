package advent.meta

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Paths
import java.time.LocalDate

object InputFile {
  fun save(client: HttpClient, date: LocalDate = LocalDate.now()) {
    val request = HttpRequest.newBuilder()
      .uri(URI.create("https://adventofcode.com/${date.year}/day/${date.dayOfMonth}/input"))
      .build()

    val target = Paths.get("src", "main", "kotlin", "advent", "year${date.year}", "day${date.dayOfMonth}", "input.txt")

    client.send(request, HttpResponse.BodyHandlers.ofFile(target))
  }
}

/**
 * Download today's input file and save it in the appropriate location.
 *
 * If you want some file other than today, provide it as an argument to "save".
 */
fun main() {
  InputFile.save(AdventOfCodeCookies.clientWithCookies())
}
