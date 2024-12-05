package advent.meta

import java.io.File

/**
 * When called from a main function, reads the "input.txt" file from the same source directory,
 * based on the package.
 */
fun readInput(): String {
    // Can't do a "this.class" because a top-level method doesn't have access to an instance object.
    val className = Thread.currentThread().stackTrace.last().className
    val packageName = className.substringBeforeLast(".")
    val path = "src/main/kotlin/${packageName.replace('.', '/')}/input.txt"
    return File(path).readText().trim()
}