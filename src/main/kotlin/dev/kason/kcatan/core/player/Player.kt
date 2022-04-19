package dev.kason.kcatan.core.player

import javafx.scene.paint.Color as JFXColor
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

        val formalName: String
            get() = when (this) {
                RED -> "red"
                BLUE -> "blue"
                WHITE -> "white"
                ORANGE -> "orange"
            }

        val jfxColor: JFXColor
            get() = when (this) {
                BLUE -> JFXColor.web("5aa2ff")
                WHITE -> JFXColor.web("fafafa")
                ORANGE -> JFXColor.web("ffb75a")
                RED -> JFXColor.web("ff655a")
            }

    }
}