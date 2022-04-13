package dev.kason.kcatan.core.player

import dev.kason.kcatan.core.PlayerResources
import dev.kason.kcatan.core.board.Edge
import dev.kason.kcatan.core.board.Intersection

class Player(
    val id: Int,
    val color: Color
) {

    val settlements = mutableListOf<Intersection>()
    val roads = mutableListOf<Edge>()
    var knightPoints = 0
    val resources: PlayerResources = PlayerResources(this)
    val developmentCards = mutableListOf<DevCard>()

    fun calculateVictoryPoints(): Int {
        TODO("Not yet implemented")
    }

    fun calculateLongestRoad(): Int {
        TODO("Not yet implemented")
    }

    enum class Color {
        RED,
        BLUE,
        WHITE,
        ORANGE;

        fun formalName(): String {
            return when (this) {
                RED -> "red"
                BLUE -> "blue"
                WHITE -> "white"
                ORANGE -> "orange"
            }
        }
    }
}