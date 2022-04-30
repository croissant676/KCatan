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
import tornadofx.find
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
    val gameName: String
) {

    companion object Sample : KLogging() {
        fun createGameFromSettings() = Game(
            Random(GameCreationSettings.seed),
            GameCreationSettings.numberOfPlayers,
            gameName = GameCreationSettings.gameName
        )
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

    fun longestRoad(player: Player): Int {
        //find groups of roads
        var roadGroups = mutableSetOf<MutableSet<Edge>>()
        var unmarkedRoads = player.roads.toMutableSet()
        fun findGroup(edge: Edge): MutableSet<Edge> {
            var group = mutableSetOf<Edge>()
            group.add(edge)
            unmarkedRoads.remove(edge)
            edge.vertices.forEach { vertex ->
                if (vertex.player == player || vertex.player == null) {
                    vertex.edges.forEach { edge1 ->
                        if (edge1 != edge && edge1.player == player && unmarkedRoads.contains(edge1)) {
                            group.addAll(findGroup(edge1))
                        }
                    }
                }
            }
            return group
        }
        player.roads.forEach {
            if (unmarkedRoads.contains(it)) {
                roadGroups.add(findGroup(it))
            }
        }
        var longest = 0;
        //find longest road in each group     needle: a road with only 1 vertex that has another road connected to it
        //case 1: no needles
        //case 2: 1 or 2 needles
        //case 3: 3+ needles
        roadGroups.forEach {
            //count needles
            var needles = mutableListOf<Edge>()
            it.forEach {
                var count = 0
                it.vertices.forEach {
                    var roadCount = 0
                    it.edges.forEach {
                        if (it.player == player) {
                            roadCount++
                        }
                    }
                    if (roadCount > 1) {
                        count++
                    }
                }
                if (count > 0) {
                    needles.add(it)
                }
            }
            when (needles.size) {
                0->if (longest < it.size) longest = it.size
                else->{
                    for (i in 0 until needles.size) {
                        var length = roadDFS(player, needles.get(i), it).size
                        if (longest < length) longest = length
                    }
                }
            }
        }
        return longest
    }

    fun roadDFS (player: Player, edge: Edge, unmarkedRoads: MutableSet<Edge>): MutableSet<Edge> {
        var roads = mutableSetOf<Edge>()
        roads.add(edge)
        unmarkedRoads.remove(edge)
        edge.vertices.forEach { vertex ->
            if (vertex.player == player || vertex.player == null) {
                vertex.edges.forEach { edge1 ->
                    if (edge1 != edge && edge1.player == player && unmarkedRoads.contains(edge1)) {
                        roads.addAll(roadDFS(player, edge1, unmarkedRoads))
                    }
                }
            }
        }
        return roads
    }
}

typealias RollResults = Pair<Int, Int>

fun RollResults.sum() = first + second