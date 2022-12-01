package advent.meta

/**
 * The creator of Advent of Code has requested that we include our contact information in the User-Agent header of each
 * automated request.
 *
 * See https://www.reddit.com/r/adventofcode/comments/z9dhtd/please_include_your_contact_info_in_the_useragent/
 */
object AdventOfCodeUserAgent {
  private val githubUrl = "https://github.com/samlindsaylevine/advent"

  // Breaking up my e-mail a little just in case someone scrapes repos and spams the e-mails therein.
  // Yes, I know this address is already included with every commit.
  private val emailUsername = "slindsaylevine+github"
  private val emailDomain = "gmail.com"
  private val email = "$emailUsername@$emailDomain"
  val HEADER = "$githubUrl by $email"
}