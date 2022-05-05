/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core

import dev.kason.catan.core.board.*
import dev.kason.catan.core.player.*
import dev.kason.catan.ui.GameCreationSettings
import java.util.Collections
import mu.KLogging
import tornadofx.withEach
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

    companion object : KLogging() {
        fun createGameFromSettings() = Game(
            Random(GameCreationSettings.seed),
            GameCreationSettings.numberOfPlayers,
            gameName = GameCreationSettings.gameName
        )
    }
    var currentPlayerIndex = 0
    val currentPlayer get() = players[currentPlayerIndex]
    var roll: RollResults = 6 to 6
        private set
    var currentLongestRoadPlayer: Player by Delegates.notNull()
    var currentTurn: Turn = Turn()
        private set

    data class Turn(
        var rolledDice: Boolean = false,
        var usedDevelopmentCard: Boolean = false
    )

    val developmentCardDeck by lazy {
        ArrayDeque(DevCardType.values().flatMap { Collections.nCopies(it.numberOfCards, it) }.shuffled(random))
    }

    fun generateRoll(): RollResults = (random.nextInt(1..6) to random.nextInt(1..6)).apply {
        roll = this
        currentTurn.rolledDice = true
        if (roll.sum() == 7) {
            cutCards()
            activateRobber(currentPlayer)
        } else {
            giveResources(roll.sum())
        }
    }

    fun buyDevelopmentCard(player: Player) {
        val card = developmentCardDeck.removeFirst()
        player.developmentCards[card] = player.developmentCards[card]!! + 1
    }

    fun giveResources(number: Int) = board.withEach {
        if (this.value != number) return@withEach
        for (vertex in vertices.values) {
            val player = vertex.player ?: continue
            player.resources += type.resource!!
            if (vertex.isCity) player.resources += type.resource!!
        }
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

    fun buildRoad(player: Player, edge: Edge, doChecks: Boolean = true) {
        if (doChecks) {
            require(canBuildRoad(player, edge))
        }
        edge.player = player
        player.resources -= Constants.roadCost
        player.roads += edge
    }

    fun canBuildSettlement(player: Player, vertex: Vertex): Boolean {
        if (player.resources doesNotHave Constants.settlementCost) return false
        if (vertex.hasConstruction) return false
        if (vertex.edges.none { it.player == player }) return false
        if (player.settlements.size > Constants.maxSettlements) return false
        return true
    }

    fun buildSettlement(player: Player, vertex: Vertex, doChecks: Boolean = true) {
        if (doChecks) {
            require(canBuildSettlement(player, vertex))
        }
        vertex.player = player
        player.resources -= Constants.settlementCost
        player.settlements += vertex
    }

    fun canBuildCity(player: Player, vertex: Vertex): Boolean {
        if (player.resources doesNotHave Constants.cityCost) return false
        if (!vertex.isSettlement) return false
        if (vertex.player != player) return false
        if (player.cities.size > Constants.maxCities) return false
        return true
    }

    fun buildCity(player: Player, vertex: Vertex, doChecks: Boolean = true) {
        if (doChecks) {
            require(canBuildCity(player, vertex))
        }
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
        edges -= player.roads.toSet()
        return edges.filter { it.isEmpty }
    }

    fun getPossibleSettlements(player: Player): List<Vertex> {
        val possibleSettlements = mutableSetOf<Vertex>()
        player.roads.flatMapTo(possibleSettlements) { it.vertices }
        val finalSettlements = possibleSettlements.toMutableSet()
        for (possibleSettlement in possibleSettlements) {
            for (vertex in possibleSettlement.vertices) {
                if (!vertex.isEmpty) {
                    finalSettlements -= possibleSettlement
                    break
                }
            }
        }
        return finalSettlements.filter { it.isEmpty }
    }

    fun getPossibleSettlementsInit(player: Player): List<Vertex> {
        val possibleSettlements = board.vertices.toList()
        var finalSettlements = possibleSettlements.toList()
        for (possibleSettlement in possibleSettlements) {
            for (vertex in possibleSettlement.vertices) {
                if (vertex.player != null) {
                    finalSettlements -= possibleSettlement
                    break
                }
                for (adjacentVertex in vertex.vertices) {
                    if (adjacentVertex.player != null && adjacentVertex != possibleSettlement) {
                        finalSettlements -= possibleSettlement
                        break
                    }
                }
            }
        }
        return finalSettlements
    }

    fun getPossibleSettlementsInit(): List<Vertex> {
        val possibleSettlements = board.vertices.toSet()
        val finalSettlements = possibleSettlements.toMutableSet()
        for (possibleSettlement in possibleSettlements) {
            for (vertex in possibleSettlement.vertices) {
                if (!vertex.isEmpty) {
                    finalSettlements -= possibleSettlement
                    break
                }
            }
        }
        return finalSettlements.filter { it.isEmpty }
    }

    fun getLongestRoadPlayer(): Player? {
        val lengths = players.associateWith { game.longestRoad(it).size }
        val max = lengths.values.maxOrNull() ?: 0
        if (lengths[currentLongestRoadPlayer] == max) return currentLongestRoadPlayer
        val list = lengths.keys.filter { lengths[it] == max }
        currentLongestRoadPlayer = when {
            list.size == 1 -> list.first()
            currentLongestRoadPlayer in list -> list.first()
            else -> list.random()
        }
        if (max < 5) return null // We still calculate the longest road if it's less than 5
        return currentLongestRoadPlayer
    }

    fun longestRoad(player: Player): List<Edge> {
        val roadGroups = mutableSetOf<MutableSet<Edge>>()
        val unmarkedRoads = player.roads.toMutableSet()
        fun findGroup(edge: Edge): MutableSet<Edge> {
            val group = mutableSetOf<Edge>()
            group += edge
            unmarkedRoads -= edge
            for (vertex in edge.vertices) {
                if (vertex.player == player || vertex.player == null) {
                    for (vertexEdges in vertex.edges) {
                        if (vertexEdges != edge && vertexEdges.player == player && vertexEdges in unmarkedRoads) group += findGroup(
                            vertexEdges
                        )
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
        var longest = mutableListOf<Edge>()
        for (roadGroup in roadGroups) {
            val needles = mutableListOf<Edge>()
            for (edge in roadGroup) {
                var count = 0
                for (vertex in edge.vertices) {
                    if (vertex.edges.filter { it.player == player }.size > 1) count++
                }
                if (count == 1) needles += edge
            }
            when (needles.size) {
                0 -> {
                    val temp = roadGroup.toList()
                    for (edge in temp) {
                        val list = roadDFS(player, edge, null, roadGroup.toMutableSet())
                        if (longest.size < list.size) longest = list
                    }
                }
                else -> {
                    for (index in needles.indices) {
                        val list = roadDFS(player, needles[index], null, roadGroup.toMutableSet())
                        if (longest.size < list.size) longest = list
                    }
                }
            }
        }
        return longest
    }

    fun roadDFS(
        player: Player,
        edge: Edge,
        previousVertex: Vertex?,
        unmarkedRoads: MutableSet<Edge>
    ): MutableList<Edge> {
        val roads = mutableListOf<Edge>()
        roads.add(edge)
        unmarkedRoads.remove(edge)
        edge.vertices.forEach { vertex ->
            if (vertex.player == player || vertex.player == null && vertex != previousVertex) {
                var longest = mutableListOf<Edge>()
                vertex.edges.forEach { edge1 ->
                    if (edge1 != edge && edge1.player == player && unmarkedRoads.contains(edge1)) {
                        val route = roadDFS(player, edge1, vertex, unmarkedRoads)
                        if (route.size > longest.size) longest = route
                    }
                }
                roads += longest
            }
        }
        return roads
    }

    fun cutCards(): List<Player> = players.filter { it.resources.values.sum() >= 7 }

    private fun activateRobber(currentPlayer: Player) {
        TODO("Not yet implemented")
    }

    @Deprecated(
        "Testing only",
        ReplaceWith("board.edges.forEach { it.player = players.random() }")
    )
    fun generateEdgesRandomly() {
        board.edges.forEach {
            it.player = players.random()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Game) return false
        if (players != other.players) return false
        if (gameName != other.gameName) return false
        if (board != other.board) return false
        if (developmentCardDeck != other.developmentCardDeck) return false
        return true
    }

    override fun hashCode(): Int {
        var result = random.hashCode()
        result = 31 * result + players.hashCode()
        result = 31 * result + gameName.hashCode()
        result = 31 * result + board.hashCode()
        result = 31 * result + developmentCardDeck.hashCode()
        return result
    }


}


typealias RollResults = Pair<Int, Int>

fun RollResults.sum() = first + second