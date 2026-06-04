package com.example.mercader.common.utils

fun String.parseErrorMessage(): String {
    return this
        .takeIf { it.contains("\"error\":\"") }
        ?.substringAfter("\"error\":\"")
        ?.substringBefore("\"}")
        ?: this
}