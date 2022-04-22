/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core.test

import dev.kason.catan.core.board.Board
import java.io.File
import kotlin.random.Random

fun main() {
    val file = File("C:\\Users\\crois\\IdeaProjects\\KCatan\\src\\main\\resources\\fxml\\board.fxml")
    val lines = file.readLines()
    lines.forEach {
        println(it)
    }
    val newLines = lines.map { s ->
        s.split("\"").joinToString(separator = "\"") {
            val x = it.toDoubleOrNull()
            if (x != null) (x / 2).toString() else it
        }
    }
    println(newLines.joinToString(separator = "\n"))
    file.writeText(newLines.joinToString(separator = "\n"))
}