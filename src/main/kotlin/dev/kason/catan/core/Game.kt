/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core

import dev.kason.catan.core.board.*
import dev.kason.catan.core.player.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import mu.KotlinLogging
import kotlin.random.Random
import kotlin.random.nextInt

@Suppress("MemberVisibilityCanBePrivate")
open class Game(
    val random: Random = Random,
    numberOfPlayers: Int = 4,
    playerOrder: List<Player.Color> = Player.Color.values()
        .run { toMutableList().shuffled(random).subList(0, numberOfPlayers) },
    val gameName: String = LocalDateTime.now().toString()
) {
    companion object Sample : Game(Random(6)) {
        val logger = KotlinLogging.logger {}
    }

    val board = Board(random)
    val players = playerOrder.mapIndexed(::Player)
    var currentPlayerIndex = 0
        private set
    val currentPlayer get() = players[currentPlayerIndex]
    var roll: RollResults = 3 to 4
        private set

    var currentTurn: Turn = Turn()
        private set

    data class Turn(
        var rolledDice: Boolean = false,
    )

    val developmentCardDeck by lazy {

    }

    fun generateRoll(): RollResults = (random.nextInt(1..6) to random.nextInt(1..6)).apply {
        roll = this
        currentTurn.rolledDice = true
    }
    fun nextPlayer(): Player {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size
        currentTurn = Turn()
        return currentPlayer
    }

    fun canBuildRoad(player: Player, edge: Edge): Boolean {
        if (player.resources doesNotHave Constants.roadCost) return false
        if (!edge.isEmpty) return false
        if (player.roads.size > Constants.maxRoads) return false
        return true
    }

    fun canBuildSettlement(player: Player, vertex: Vertex): Boolean {
        if (player.resources doesNotHave Constants.settlementCost) return false
        if (vertex.hasConstruction) return false
        if (vertex.edges.none { it.player == player }) return false
        if (player.settlements.size > Constants.maxSettlements) return false
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
        if (vertex.player != player) return false
        if (player.cities.size > Constants.maxCities) return false
        return true
    }

    fun buildCity(player: Player, vertex: Vertex) {
        require(canBuildCity(player, vertex))
        vertex.isCity = true
        player.resources -= Constants.cityCost
    }
}

typealias RollResults = Pair<Int, Int>

fun RollResults.sum() = first + second