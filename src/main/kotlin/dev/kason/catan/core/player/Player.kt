/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core.player

import javafx.scene.paint.Color as JFXColor
import dev.kason.catan.core.board.*

data class Player(val id: Int, val color: Color) {
    val roads: MutableList<Edge> = mutableListOf()
    val settlements: MutableList<Vertex> = mutableListOf()
    val resources = PlayerResourceMap(this)
    val cities get() = settlements.filter { it.isCity }
    val developmentCards = mutableMapOf<DevCardType, Int>()
    val name get() = color.name

    @Suppress("MemberVisibilityCanBePrivate")
    enum class Color {
        Red,
        Blue,
        White,
        Orange;

        val colorHex: String
            get() = when (this) {
                Red -> "fe0040"
                Blue -> "5ca4e5"
                White -> "efefef"
                Orange -> "ffba67"
            }
        val jfxColor: JFXColor get() = JFXColor.web("#${colorHex}")
    }

    fun giveRoad(edge: Edge) {
        roads += edge
        edge.player = this
        //recalculate longest road
    }

    fun accessiblePorts(): List<Port> = settlements.fold(mutableListOf()) { acc, vertex ->
        vertex.port?.let { acc += (it) }
        acc
    }
}