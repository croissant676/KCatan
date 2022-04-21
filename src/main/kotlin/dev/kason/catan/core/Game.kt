/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core

import dev.kason.catan.core.board.*
import dev.kason.catan.core.player.*
import kotlin.random.Random
import kotlin.random.nextInt

@Suppress("MemberVisibilityCanBePrivate")
class Game(
    val random: Random = Random,
    playerOrder: List<Player.Color>
) {
    val board = Board(random)
    val players = playerOrder.mapIndexed(::Player)
    var currentPlayerIndex = 0
        private set
    val currentPlayer get() = players[currentPlayerIndex % players.size]
    var roll: RollResults = 0 to 0
        private set
    val developmentCardDeck by lazy { }

    fun generateRoll(): RollResults = (random.nextInt(0..6) to random.nextInt(0..6)).also { roll = it }
    fun nextPlayer(): Player = players[(currentPlayerIndex++) % players.size]

    fun canBuildRoad(player: Player, edge: Edge): Boolean {
        if (player.resources doesNotHave Constants.roadCost) return false
        if (edge.player != null) return false
        return true
    }

    fun canBuildSettlement(player: Player, vertex: Vertex): Boolean {
        if (player.resources doesNotHave Constants.settlementCost) return false
        if (vertex.hasConstruction) return false
        if (vertex.edges.none { it.player == player }) return false
        return true
    }

    fun buildSettlement(player: Player, vertex: Vertex) {
        require(canBuildSettlement(player, vertex))
        vertex.player = player
        player.resources -= Constants.settlementCost
    }

    fun canBuildCity(player: Player, vertex: Vertex): Boolean {
        if (player.resources doesNotHave Constants.cityCost) return false
        if (!vertex.isSettlement) return false
        return true
    }

    fun buildCity(player: Player, vertex: Vertex) {
        require(canBuildCity(player, vertex))
        vertex.isCity = true
        player.resources -= Constants.cityCost
    }

}

typealias RollResults = Pair<Int, Int>