package com.delta.mobileplatform.web.controller.utils.string


/*
* Since webview evaluateJs function pass string will decrease escape characters
* */
fun addSlashes(str: String): String {
//    return str.split("\\\"").joinToString("\\\\\\\"") { return@joinToString it.replace("\"", "\\\"") }
    return buildString {
        for (char in str) {
            when (char) {
                '\"' -> append("\\\"")
                '\\' -> append("\\\\")
                '\b' -> append("\\b")
                '\u000C' -> append("\\f")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                in '\u0000'..'\u001F' -> append("\\u%04x".format(char.code))
                else -> append(char)
            }
        }
    }

}