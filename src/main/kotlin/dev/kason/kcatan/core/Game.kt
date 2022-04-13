package dev.kason.kcatan.core

import dev.kason.kcatan.core.board.Edge
import dev.kason.kcatan.core.board.Intersection
import dev.kason.kcatan.core.player.*
import java.util.Collections
import kotlin.random.Random
import kotlin.random.nextInt

private val diceRange = 1..6

class Game(val random: Random = Random, playerOrder: List<Player.Color>) {
    val players = playerOrder.mapIndexed { index, color -> Player(index, color) }
    var currentPlayerTurn = 0
    val currentPlayer get() = players[currentPlayerTurn]
    var diceRolls = 0 to 0
    val diceRollValue get() = diceRolls.first + diceRolls.second

    var longestRoadPlayer: Player? = null
    var longestRoadLength = 0

    // use removeFirst() to get the first element, add() to add the element to the end
    val deck = ArrayDeque(DevCard.values().flatMap {
        when (it) {
            DevCard.KNIGHT -> Collections.nCopies(14, it)
            DevCard.VICTORY -> Collections.nCopies(5, it)
            else -> Collections.nCopies(2, it)
        }
    }.shuffled())

    fun nextPlayerTurn(): Player = players[currentPlayerTurn++ % players.size]
    fun checkWinConditions(): Player? = players.firstOrNull { it.calculateVictoryPoints() > 10 }
    fun rollDie(): Pair<Int, Int> {
        diceRolls = random.nextInt(diceRange) to random.nextInt(diceRange)
        return diceRolls
    }

    fun buildRoad(player: Player, edge: Edge): Boolean {
        if (edge.hasRoad) return false
        if (!player.resources.hasResources(Costs.road)) return false
        edge.player = player
        player.roads += edge
        player.resources -= Costs.road
        return true
    }

    fun buildSettlement(player: Player, intersection: Intersection): Boolean {
        if (intersection.hasConstruction) return false
        if (!player.resources.hasResources(Costs.settlement)) return false
        intersection.player = player
        player.settlements.add(intersection)
        player.resources -= Costs.settlement
        return true
    }

    fun buildCity(player: Player, intersection: Intersection): Boolean {
        if (intersection.isCity) return false
        if (!player.resources.hasResources(Costs.city)) return false
        intersection.isCity = true
        player.resources -= Costs.city
        return true
    }


    fun buildDevelopmentCard(player: Player): DevCard {
        val draw = deck.removeFirst()
        player.developmentCards += draw
        player.resources -= Costs.developmentCard
        return draw
    }

    fun findLongestRoad(): Int {
        val playerLengths = players.map { it.calculateLongestRoad() }
        playerLengths.forEachIndexed { index, length ->
            if (length > longestRoadLength) {
                longestRoadLength = length
                longestRoadPlayer = if (length <= 5) {
                    null
                } else {
                    players[index]
                }
            }
        }
        return longestRoadLength
    }
}