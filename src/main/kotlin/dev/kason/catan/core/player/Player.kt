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

    init {
        DevCardType.values().forEach {
            developmentCards[it] = 0
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    enum class Color {
        Red,
        Blue,
        White,
        Orange;

        val colorHex: String
            get() = when (this) {
                Red -> "ff3a3a"
                Blue -> "5ca4e5"
                White -> "efefef"
                Orange -> "ffba67"
            }
        val jfxColor: JFXColor get() = JFXColor.web("#${colorHex}")
    }

    fun accessiblePorts(): Set<Port> = settlements.mapNotNull { it._port }.toSet()
}