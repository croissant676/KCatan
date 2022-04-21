/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core.board

import java.util.EnumMap
import java.util.EnumSet

enum class Location {
    Top,
    TopLeft,
    TopRight,
    Left,
    Right,
    Bottom,
    BottomLeft,
    BottomRight;

    companion object {
        val tileLocations = EnumSet.of(TopLeft, TopRight, Left, Right, BottomLeft, BottomRight)!!
        val edgeLocations = EnumSet.of(TopLeft, TopRight, Left, Right, BottomLeft, BottomRight)!!
        val vertexLocations = EnumSet.of(Top, TopLeft, TopRight, Bottom, BottomLeft, BottomRight)!!
        val checkLocations = EnumSet.of(TopLeft, TopRight, Left)!!
    }

    val opposite: Location
        get() = when (this) {
            Top -> Bottom
            TopLeft -> BottomRight
            TopRight -> BottomLeft
            Left -> Right
            Right -> Left
            Bottom -> Top
            BottomLeft -> TopRight
            BottomRight -> TopLeft
        }
}

typealias LocationMap<T> = Map<Location, T>
typealias LocationMutableMap<T> = MutableMap<Location, T>

fun <T> createLocationMap(): LocationMutableMap<T> = EnumMap(Location::class.java)