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
        val cookie = HttpCookie(it.name, it.value).apply {
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