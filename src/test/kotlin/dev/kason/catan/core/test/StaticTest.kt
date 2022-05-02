/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core.test

import dev.kason.catan.core.board.Board
import dev.kason.catan.core.board.Location
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlin.random.Random

private val json = Json { prettyPrint = true }

fun main() {
    val board = Board(Random)
//    mapOf(
//        Location.TopLeft to Recording(295, 267, 0, -30, -50, 0),
//        Location.TopRight to Recording(295, 267, 0, -30, 53, 0),
//        Location.Right to Recording(295, 267, 53, 0, 53, 63),
//        Location.BottomRight to Recording(295, 267, 53, 63, 2, 93),
//        Location.BottomLeft to Recording(295, 267, -50, 63, 2, 93),
//        Location.Left to Recording(295, 267, -50, 0, -50, 63)
//    ).mapValues { it.value.change() }.also { println(json.encodeToString(it)) }
}

@Serializable
data class Recording(
    @Transient
    val layoutX: Int = 0,
    @Transient
    val layoutY: Int = 0,
    val startX: Int,
    val startY: Int,
    val endX: Int,
    val endY: Int
) {
    fun change(): Recording {
        val newStartX = layoutX + startX - 357
        val newStartY = layoutY + startY - 303
        val newEndX = layoutX + endX - 357
        val newEndY = layoutY + endY - 303
        return Recording(0, 0, newStartX, newStartY, newEndX, newEndY)
    }
}