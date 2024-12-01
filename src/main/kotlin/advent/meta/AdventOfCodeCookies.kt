package advent.meta

import cmonster.browsers.ChromeBrowser
import cmonster.cookies.Cookie
import java.net.CookieManager
import java.net.HttpCookie
import java.net.URI
import java.net.http.HttpClient

/**
 * Gets our session cookie for use in making requests to the Advent of Code page to get our puzzle input or a problem
 * description. Uses the "CookieMonster" library for exfiltrating cookies from the web browser.
 */
class AdventOfCodeCookies {
  companion object {
    private fun retrieveFromChrome(): Set<Cookie> {
      val chrome = ChromeBrowser()
      return chrome.getCookiesForDomain("adventofcode.com")
    }

    fun clientWithCookies(): HttpClient {
      val cookieHandler = CookieManager()
      retrieveFromChrome().forEach {
        // Our cookie-retrieving library is getting some bogus values - it has padded mojibake at the front of the
        // actual cookie value.
        // e.g., our cookie value is coming back as "m}?CUf�u5�>@v�ê?���k6���χ��53616c[...]b5fc5"
        // when we see in the browser developer tools that it should be just "53616c[...]b5fc5".
        // We're going to implement a sort of dorky workaround here to take only the final part that starts being normal
        // ASCII text.
        val asciiValue = it.value.takeLastWhile { char -> char.code < 128}
        val cookie = HttpCookie(it.name, asciiValue).apply {
          path = "/"
          version = 0
        }
        cookieHandler.cookieStore.add(URI("https://adventofcode.com"), cookie)
      }

      return HttpClient.newBuilder()
        .cookieHandler(cookieHandler)
        .build()
    }
  }
}