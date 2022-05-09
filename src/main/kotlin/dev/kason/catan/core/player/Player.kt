/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core.player

import javafx.scene.paint.Color as JFXColor
import dev.kason.catan.core.Constants
import dev.kason.catan.core.board.*
import dev.kason.catan.core.game

data class Player(val id: Int, val color: Color) {
    val roads: MutableList<Edge> = mutableListOf()
    val settlements: MutableList<Vertex> = mutableListOf()
    val resources = PlayerResourceMap(this)
    val cities get() = settlements.filter { it.isCity }
    val developmentCards = mutableMapOf<DevCardType, Int>()
    val name get() = color.name

    var devCardVP = 0
    var armyStrength = 0
    val totalVP: Int
        get() = visibleVP + devCardVP

    val visibleVP: Int
        get() {
            var sum = settlements.count { it.isCity } + settlements.size
            if (game.calculateLongestRoad() == this) sum += 2
            if (game.largestArmy() == this) sum += 2
            return sum
        }

    init {
        DevCardType.values().forEach {
            developmentCards[it] = 5
        }
    }

    val roadsLeft: Int get() = Constants.maxRoads - roads.size
    val settlementsLeft: Int get() = Constants.maxSettlements - settlements.size
    val citiesLeft: Int get() = Constants.maxCities - cities.size

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