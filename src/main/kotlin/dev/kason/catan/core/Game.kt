/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core

import dev.kason.catan.core.board.*
import dev.kason.catan.core.player.*
import dev.kason.catan.ui.GameCreationSettings
import mu.KLogging
import kotlin.properties.Delegates
import kotlin.random.Random
import kotlin.random.nextInt

var game by Delegates.notNull<Game>()

@Suppress("MemberVisibilityCanBePrivate")
open class Game(
    val random: Random = Random,
    numberOfPlayers: Int = 4,
    playerOrder: List<Player.Color> = Player.Color.values()
        .run { toMutableList().shuffled(random).subList(0, numberOfPlayers) },
    val players: List<Player> = playerOrder.mapIndexed(::Player),
    val gameName: String = "TestGame",
    val board: Board = Board(random)
) {

    companion object Sample : KLogging() {
        fun createGameFromSettings() = Game(
            Random(GameCreationSettings.seed),
            GameCreationSettings.numberOfPlayers,
            gameName = GameCreationSettings.gameName
        )
    }
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
        ArrayDeque(
            DevCardType.values().toMutableList().shuffled(random)
        )
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

    fun buildRoad(player: Player, edge: Edge) {
        require(canBuildRoad(player, edge))
        edge.player = player
        player.resources -= Constants.roadCost
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

    fun getPossibleRoads(player: Player): List<Edge> {
        val edges = mutableSetOf<Edge>()
        player.roads.forEach {
            edges += it.edges
        }
        player.settlements.forEach {
            edges += it.edges
        }
        return edges.filter { it.isEmpty }
    }

    fun getPossibleSettlements(player: Player): List<Vertex> {
        val possibleSettlements = mutableSetOf<Vertex>()
        player.roads.flatMapTo(possibleSettlements) { it.vertices }
        for (possibleSettlement in possibleSettlements) {
            for (vertex in possibleSettlement.vertices) {
                if (vertex.hasConstruction) {
                    possibleSettlements -= possibleSettlement
                    break
                }
                for (adjacentVertex in vertex.vertices) {
                    if (adjacentVertex.player != null && adjacentVertex != possibleSettlement) {
                        possibleSettlements -= possibleSettlement
                        break
                    }
                }
            }
        }
        return possibleSettlements.toList()
    }

    fun longestRoad(player: Player): Int {
        val roadGroups = mutableSetOf<MutableSet<Edge>>()
        val unmarkedRoads = player.roads.toMutableSet()
        fun findGroup(edge: Edge): MutableSet<Edge> {
            val group = mutableSetOf<Edge>()
            group += edge
            unmarkedRoads -= edge
            for (vertex in edge.vertices) {
                if (vertex.player == player || vertex.player == null) {
                    for (vertexEdges in vertex.edges) {
                        if (vertexEdges != edge && vertexEdges.player == player && vertexEdges in unmarkedRoads) group += findGroup(vertexEdges)
                    }
                }
            }
            return group
        }
        while (unmarkedRoads.isNotEmpty()) {
            roadGroups += findGroup(unmarkedRoads.first())
        }
        roadGroups.forEach {
            logger.debug(it.toString())
        }
        //player.roads.filter { it in unmarkedRoads }.mapTo(roadGroups) { findGroup(it) }
        var longest = 0
        for (edges in roadGroups) {
            val needles = mutableListOf<Edge>()
            for (edge in edges) {
                var count = 0
                for (vertex in edge.vertices) {
                    if (vertex.edges.filter { it.player == player }.size > 1) count++
                }
                if (count > 0) needles += edge
            }
            when (needles.size) {
                0 -> if (longest < edges.size) longest = edges.size
                else -> {
                    for (index in needles.indices) {
                        val length = roadDFS(player, needles[index], edges).size
                        if (longest < length) longest = length
                    }
                }
            }
        }
        return longest
    }

    fun roadDFS(player: Player, edge: Edge, unmarkedRoads: MutableSet<Edge>): MutableSet<Edge> {
        val roads = mutableSetOf<Edge>()
        roads.add(edge)
        unmarkedRoads.remove(edge)
        edge.vertices.forEach { vertex ->
            if (vertex.player == player || vertex.player == null) {
                var longest = mutableSetOf<Edge>()
                vertex.edges.forEach { edge1 ->
                    if (edge1 != edge && edge1.player == player && unmarkedRoads.contains(edge1)) {
                        var route = roadDFS(player, edge1, unmarkedRoads)
                        if (route.size > longest.size) longest = route
                    }
                }
                roads += longest
            }
        }
        return roads
    }
}

typealias RollResults = Pair<Int, Int>

fun RollResults.sum() = first + second