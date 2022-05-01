/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core.board

import dev.kason.catan.core.player.ResourceType
import kotlin.math.absoluteValue

@Suppress("PropertyName")
data class Tile(
    val id: Int,
    val type: Type
) {
    internal var _value: Int = 0
    val value: Int get() = _value
    internal val _neighbors: LocationMutableMap<Tile> = createLocationMap()
    internal val _edges: LocationMutableMap<Edge> = createLocationMap()
    internal val _vertices: LocationMutableMap<Vertex> = createLocationMap()
    val neighbors: LocationMap<Tile> = _neighbors
    val edges: LocationMap<Edge> = _edges
    val vertices: LocationMap<Vertex> = _vertices

    val chance get() = 6 - (value - 7).absoluteValue

    enum class Type {
        Hills,
        Forest,
        Mountains,
        Desert,
        Fields,
        Pasture;
        val resource: ResourceType?
            get() = when (this) {
                Hills -> ResourceType.Brick
                Forest -> ResourceType.Lumber
                Mountains -> ResourceType.Ore
                Fields -> ResourceType.Grain
                Pasture -> ResourceType.Wool
                Desert -> null
            }
        val numberOfTiles: Int
            get() = when (this) {
                Desert -> 1
                Hills, Mountains -> 3
                else -> 4
            }
        val hexColor: String
            get() = when (this) {
                Hills -> "#e06f46"
                Forest -> "#529636"
                Mountains -> "#b7b7b7"
                Desert -> "#fff2cc"
                Fields -> "#ffe687"
                Pasture -> "#93c47d"
            }
    }

    override fun toString(): String {
        return "Tile(id=$id, type=$type)"
    }
}

// Tile value generation

internal fun boardRotations(number: Int, clockwise: Boolean) = ArrayDeque(
    if (clockwise) when (number) {
        0 -> listOf(0, 1, 2, 6, 11, 15, 18, 17, 16, 12, 7, 3, 4, 5, 10, 14, 13, 8, 9)
        1 -> listOf(1, 2, 6, 11, 15, 18, 17, 16, 12, 7, 3, 0, 4, 5, 10, 14, 13, 8, 9)
        2 -> listOf(2, 6, 11, 15, 18, 17, 16, 12, 7, 3, 0, 1, 5, 10, 14, 13, 8, 4, 9)
        3 -> listOf(3, 0, 1, 2, 6, 11, 15, 18, 17, 16, 12, 7, 8, 4, 5, 10, 14, 13, 9)
        6 -> listOf(6, 11, 15, 18, 17, 16, 12, 7, 3, 0, 1, 2, 5, 10, 14, 13, 8, 4, 9)
        7 -> listOf(7, 3, 0, 1, 2, 6, 11, 15, 18, 17, 16, 12, 8, 4, 5, 10, 14, 13, 9)
        11 -> listOf(11, 15, 18, 17, 16, 12, 7, 3, 0, 1, 2, 6, 10, 14, 13, 8, 4, 5, 9)
        12 -> listOf(12, 7, 3, 0, 1, 2, 6, 11, 15, 18, 17, 16, 13, 8, 4, 5, 10, 14, 9)
        15 -> listOf(15, 18, 17, 16, 12, 7, 3, 0, 1, 2, 6, 11, 10, 14, 13, 8, 4, 5, 9)
        16 -> listOf(16, 12, 7, 3, 0, 1, 2, 6, 11, 15, 18, 17, 13, 8, 4, 5, 10, 14, 9)
        17 -> listOf(17, 16, 12, 7, 3, 0, 1, 2, 6, 11, 15, 18, 14, 13, 8, 4, 5, 10, 9)
        18 -> listOf(18, 17, 16, 12, 7, 3, 0, 1, 2, 6, 11, 15, 14, 13, 8, 4, 5, 10, 9)
        else -> throw IllegalArgumentException("Invalid tile number: $number")
    } else when (number) {
        0 -> listOf(0, 3, 7, 12, 16, 17, 18, 15, 11, 6, 2, 1, 4, 8, 13, 14, 10, 5, 9)
        1 -> listOf(1, 0, 3, 7, 12, 16, 17, 18, 15, 11, 6, 2, 5, 4, 8, 13, 14, 10, 9)
        2 -> listOf(2, 1, 0, 3, 7, 12, 16, 17, 18, 15, 11, 6, 5, 4, 8, 13, 14, 10, 9)
        3 -> listOf(3, 7, 12, 16, 17, 18, 15, 11, 6, 2, 1, 0, 4, 8, 13, 14, 10, 5, 9)
        6 -> listOf(6, 2, 1, 0, 3, 7, 12, 16, 17, 18, 15, 11, 10, 5, 4, 8, 13, 14, 9)
        7 -> listOf(7, 12, 16, 17, 18, 15, 11, 6, 2, 1, 0, 3, 8, 13, 14, 10, 5, 4, 9)
        11 -> listOf(11, 6, 2, 1, 0, 3, 7, 12, 16, 17, 18, 15, 10, 5, 4, 8, 13, 14, 9)
        12 -> listOf(12, 16, 17, 18, 15, 11, 6, 2, 1, 0, 3, 7, 8, 13, 14, 10, 5, 4, 9)
        15 -> listOf(15, 11, 6, 2, 1, 0, 3, 7, 12, 16, 17, 18, 14, 10, 5, 4, 8, 13, 9)
        16 -> listOf(16, 17, 18, 15, 11, 6, 2, 1, 0, 3, 7, 12, 13, 14, 10, 5, 4, 8, 9)
        17 -> listOf(17, 18, 15, 11, 6, 2, 1, 0, 3, 7, 12, 16, 13, 14, 10, 5, 4, 8, 9)
        18 -> listOf(18, 15, 11, 6, 2, 1, 0, 3, 7, 12, 16, 17, 14, 10, 5, 4, 8, 13, 9)
        else -> throw IllegalArgumentException("Invalid tile number: $number")
    }
)

internal val validFirstTiles = listOf(0, 1, 2, 3, 6, 7, 11, 12, 15, 16, 17, 18)