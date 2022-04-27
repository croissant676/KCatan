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

    private val tile0: Polygon by fxid()
    private val tile1: Polygon by fxid()
    private val tile2: Polygon by fxid()
    private val tile3: Polygon by fxid()
    private val tile4: Polygon by fxid()
    private val tile5: Polygon by fxid()
    private val tile6: Polygon by fxid()
    private val tile7: Polygon by fxid()
    private val tile8: Polygon by fxid()
    private val tile9: Polygon by fxid()
    private val tile10: Polygon by fxid()
    private val tile11: Polygon by fxid()
    private val tile12: Polygon by fxid()
    private val tile13: Polygon by fxid()
    private val tile14: Polygon by fxid()
    private val tile15: Polygon by fxid()
    private val tile16: Polygon by fxid()
    private val tile17: Polygon by fxid()
    private val tile18: Polygon by fxid()

    private val circle0: Circle by fxid()
    private val circle1: Circle by fxid()
    private val circle2: Circle by fxid()
    private val circle3: Circle by fxid()
    private val circle4: Circle by fxid()
    private val circle5: Circle by fxid()
    private val circle6: Circle by fxid()
    private val circle7: Circle by fxid()
    private val circle8: Circle by fxid()
    private val circle9: Circle by fxid()
    private val circle10: Circle by fxid()
    private val circle11: Circle by fxid()
    private val circle12: Circle by fxid()
    private val circle13: Circle by fxid()
    private val circle14: Circle by fxid()
    private val circle15: Circle by fxid()
    private val circle16: Circle by fxid()
    private val circle17: Circle by fxid()
    private val circle18: Circle by fxid()

    private val label0: Text by fxid()
    private val label1: Text by fxid()
    private val label2: Text by fxid()
    private val label3: Text by fxid()
    private val label4: Text by fxid()
    private val label5: Text by fxid()
    private val label6: Text by fxid()
    private val label7: Text by fxid()
    private val label8: Text by fxid()
    private val label9: Text by fxid()
    private val label10: Text by fxid()
    private val label11: Text by fxid()
    private val label12: Text by fxid()
    private val label13: Text by fxid()
    private val label14: Text by fxid()
    private val label15: Text by fxid()
    private val label16: Text by fxid()
    private val label17: Text by fxid()
    private val label18: Text by fxid()

    private val listOfTiles = listOf(
        tile0,
        tile1,
        tile2,
        tile3,
        tile4,
        tile5,
        tile6,
        tile7,
        tile8,
        tile9,
        tile10,
        tile11,
        tile12,
        tile13,
        tile14,
        tile15,
        tile16,
        tile17,
        tile18
    )


    private val listOfCircles = listOf(
        circle0,
        circle1,
        circle2,
        circle3,
        circle4,
        circle5,
        circle6,
        circle7,
        circle8,
        circle9,
        circle10,
        circle11,
        circle12,
        circle13,
        circle14,
        circle15,
        circle16,
        circle17,
        circle18
    )


    private val listOfLabels = listOf(
        label0,
        label1,
        label2,
        label3,
        label4,
        label5,
        label6,
        label7,
        label8,
        label9,
        label10,
        label11,
        label12,
        label13,
        label14,
        label15,
        label16,
        label17,
        label18
    )

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
            text = if (value < 10) "  $value" else value.toString()
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