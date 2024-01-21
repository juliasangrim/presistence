package org.example.extention

import javax.print.DocFlavor.STRING

fun String?.orDefault(default: String = ""): String {
    return this?: default
}

fun Boolean?.orDefault(default: Boolean = false): Boolean {
    return this?: default
}

fun Int?.orDefault(default: Int = -1): Int {
    return this?: default
}