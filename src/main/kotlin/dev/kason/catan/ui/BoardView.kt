/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.catan
import dev.kason.catan.core.board.Board
import dev.kason.catan.core.board.Tile
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.shape.Circle
import javafx.scene.shape.Polygon
import javafx.scene.text.*
import mu.KLogging
import tornadofx.*

class BoardView(
    private val board: Board
) : View(catan("Board")) {

    companion object : KLogging()

    override val root: Parent by fxml("/fxml/board.fxml")

    private val listOfTiles = List(19) {
        fxmlLoader.namespace["tile$it"] as Polygon
    }

    private val listOfCircles = List(19) {
        fxmlLoader.namespace["circle$it"] as Circle
    }

    private val listOfLabels = List(19) {
        fxmlLoader.namespace["label$it"] as Text
    }
    val hexagonRetriever = object {
        operator fun get(tile: Tile): Polygon {
            return listOfTiles[tile.id]
        }
    }

    val circleRetriever = object {
        operator fun get(tile: Tile): Circle {
            return listOfCircles[tile.id]
        }
    }

    val labelRetriever = object {
        operator fun get(tile: Tile): Text {
            return listOfLabels[tile.id]
        }
    }


    private fun Polygon.findId(): Int = listOfTiles.indexOf(this)
    private fun Circle.findId(): Int = listOfCircles.indexOf(this)
    private fun Text.findId(): Int = listOfLabels.indexOf(this)

    init {
        listOfTiles.withEach {
            val tile = board[findId()]
            val styleName = tile.type.name.lowercase()
            addClass(styleName)
            setOnMouseClicked {
                tile.infoFragment.openWindow()
            }
        }
        listOfCircles.withEach {
            val tile = board[findId()]
            if (tile.type == Tile.Type.Desert) isVisible = false
        }
        listOfLabels.withEach {
            val tile = board[findId()]
            val value = tile.value
            if (tile.type == Tile.Type.Desert) isVisible = false
            if (value == 6 || value == 8) {
                fill = c("#ff002d")
                logger.debug { "Styled ${tile.id} with red, value = ${tile.value}" }
            }
            font = Font.font("Century Gothic", 20.0)
            textAlignment = TextAlignment.CENTER
            text = if (value < 10) "  $value" else " $value"
        }
    }
}

private val tileFragmentMap = mutableMapOf<Int, TileFragment>()
internal val Tile.infoFragment: TileFragment
    get() = tileFragmentMap.getOrPut(id) {
        TileFragment(this)
    }

class TileFragment(val tile: Tile) : Fragment("Tile #${tile.id}") {
    companion object : KLogging()

    override val root: Parent by fxml("/fxml/tile_info.fxml")
    private val tileIdLabel: Label by fxid()
    private val tileValueLabel: Label by fxid()
    private val chanceLabel: Label by fxid()
    private val colorCircle: Circle by fxid()

    override fun onDock() {
        val styleName = tile.type.name.lowercase()
        logger.debug { "Displaying info panel for $tile" }
        colorCircle.addClass(styleName)
        tileIdLabel.text = "Tile #${tile.id + 1}"
        tileValueLabel.text = "Value: ${tile.value}"
        chanceLabel.text = "Chance: ${tile.chance} / 36"
    }
}