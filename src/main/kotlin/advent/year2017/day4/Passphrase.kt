package advent.year2017.day4

import com.google.common.collect.ImmutableMultiset
import com.google.common.collect.Multiset
import java.io.File

class Passphrase(val words: Multiset<String>) {

    private val letterCounts: Multiset<Multiset<Char>> = ImmutableMultiset.copyOf(words.map { letterCount(it) })

    constructor(str: String) : this(ImmutableMultiset.copyOf(str.trim().split("\\s+".toRegex())))

    fun hasDuplicateWords(): Boolean = words.any { words.count(it) > 1 }

    fun hasAnagrams(): Boolean = letterCounts.any { letterCounts.count(it) > 1 }

    private fun letterCount(word: String): Multiset<Char> =
            ImmutableMultiset.copyOf(word.toCharArray().toList())
}

fun main() {
    val passphrases = File("src/main/kotlin/advent/year2017/day4/input.txt")
            .readLines()
            .filter { it.isNotEmpty() }
            .map { Passphrase(it) }

    println(passphrases.count { !it.hasDuplicateWords() })
    println(passphrases.count { !it.hasAnagrams() })
}