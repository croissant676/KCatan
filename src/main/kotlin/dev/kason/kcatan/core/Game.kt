package dev.kason.kcatan.core

import dev.kason.kcatan.core.board.Edge
import dev.kason.kcatan.core.board.Intersection
import dev.kason.kcatan.core.player.*
import java.util.Collections
import kotlin.random.Random
import kotlin.random.nextInt

@Suppress("MemberVisibilityCanBePrivate", "unused")
class Game(val random: Random = Random, playerOrder: List<Player.Color>) {
    val players = playerOrder.mapIndexed { index, color -> Player(index, color) }
    var currentPlayerTurn = 0
    val currentPlayer get() = players[currentPlayerTurn]
    var diceRolls = 0 to 0
    val diceRollValue get() = diceRolls.first + diceRolls.second
    var longestRoadPlayer: Player? = null
        private set
    var longestRoadLength = 0
        private set
    val deck by lazy { generateDeck() }

    fun generateDeck() = ArrayDeque(DevCard.values().flatMap {
        when (it) {
            DevCard.KNIGHT -> Collections.nCopies(14, it)
            DevCard.VICTORY -> Collections.nCopies(5, it)
            else -> Collections.nCopies(2, it)
        }
    }.shuffled())

    fun nextPlayerTurn(): Player = players[currentPlayerTurn++ % players.size]
    fun checkWinConditions(): Player? = players.firstOrNull { it.calculateVictoryPoints() > 10 }
    fun rollDie(): Pair<Int, Int> {
        diceRolls = random.nextInt(1..6) to random.nextInt(1..6)
        return diceRolls
    }

    fun buildRoad(player: Player, edge: Edge): Boolean {
        if (edge.hasRoad) return false
        if (!player.resources.has(Costs.road)) return false
        edge.player = player
        player.roads += edge
        player.resources -= Costs.road
        return true
    }

    fun buildSettlement(player: Player, intersection: Intersection) {
        check(!intersection.hasConstruction)
        check(player.resources has Costs.settlement)
        intersection.player = player
        player.settlements += intersection
        player.resources -= Costs.settlement
    }

    fun buildCity(player: Player, intersection: Intersection) {
        check(!intersection.isCity)
        check(intersection.player == player)
        check(player.resources has Costs.city)
        intersection.isCity = true
        player.resources -= Costs.city
    }


    fun buildDevelopmentCard(player: Player): DevCard {
        val draw = deck.removeFirst()
        player.developmentCards += draw
        player.resources -= Costs.developmentCard
        return draw
    }

    fun findLongestRoad() {
        val lengths = players.map { it.calculateLongestRoad() }
        Array<String>(10) {""}.forEach {  }
        longestRoadLength = lengths.maxOrNull() ?: 0
        if (longestRoadLength < 5) return
        val longestRoadPlayers = lengths.indices.filter { lengths[it] == longestRoadLength }.map { players[it] }
        if (longestRoadPlayers.size == 1) longestRoadPlayer = longestRoadPlayers.first()
        else if (longestRoadPlayer !in longestRoadPlayers) longestRoadPlayer = longestRoadPlayers.random(random)
    }
}