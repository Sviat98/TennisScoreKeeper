package com.bashkevich.tennisscorekeeper.core

fun findNthOccurrence(str: String, char: Char, n: Int): Int {
    var currentIndex = -1
    repeat(n) {
        currentIndex = str.indexOf(char, currentIndex + 1)
        if (currentIndex == -1) return -1
    }
    return currentIndex
}