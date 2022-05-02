/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.core.Constants
import dev.kason.catan.core.board.Board
import dev.kason.catan.core.board.Vertex
import dev.kason.catan.core.player.Player
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Polygon
import tornadofx.Fragment
import tornadofx.c

class RoadSelectionFragment() : Fragment() {
    override val root: Parent by fxml("/fxml/board.fxml")
}

class SettlementSelectionFragment() : Fragment() {
    override val root: Parent by fxml("/fxml/board.fxml")

}

class CitySelectionFragment(player: Player) : Fragment() {
    override val root: Parent by fxml("/fxml/board.fxml")
    private val listOfTiles = List(19) {
        fxmlLoader.namespace["tile$it"] as Polygon
    }

    init {
        player.settlements.forEach {
            it.drawCircle(c("93c47d"), isCity = true)
        }
    }

    private fun Vertex.drawCircle(color: Color = Color.TRANSPARENT, isCity: Boolean = false) {
        for (location in tiles.keys) {
            val mainTile = tiles[location]!!
            val tileHexagon = listOfTiles[mainTile.id]
            val translationValue = Constants.pointTranslations[location]!!
            val circle = Circle(
                tileHexagon.layoutX + translationValue.x,
                tileHexagon.layoutY + translationValue.y,
                if (isCity) Constants.cityRadius else Constants.settlementRadius
            )
            circle.fill = Color.BLACK
            circle.isVisible = true
            add(circle)
        }
    }
}

class RobberSelectionFragment(val board: Board): Fragment() {
    override val root: Parent by fxml("/fxml/board.fxml")
}