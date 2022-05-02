/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.catan
import dev.kason.catan.core.Constants
import dev.kason.catan.core.Game
import dev.kason.catan.core.board.*
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.shape.*
import javafx.scene.text.*
import mu.KLogging
import tornadofx.*

@Suppress("LocalVariableName")
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

    private val listOfBoats = List(9) {
        fxmlLoader.namespace["boat$it"] as Polygon
    }

    private val listOfSails = List(9) {
        fxmlLoader.namespace["sail$it"] as Polygon
    }

    private val mapOfEdges: Map<Edge, Line> = run {
        logger.debug { "Creating lines." }
        val _mapOfEdges = mutableMapOf<Edge, Line>()
        for (edge in board.edges) {
            val mainTile = edge.first
            val location = mainTile.edges.keys.first { mainTile.edges[it] == edge }
            val tileHexagon = listOfTiles[mainTile.id]
            val translationValue = Constants.lineTranslations[location]!!
            val line = Line(
                tileHexagon.layoutX + translationValue.startX,
                tileHexagon.layoutY + translationValue.startY,
                tileHexagon.layoutX + translationValue.endX,
                tileHexagon.layoutY + translationValue.endY
            )
            _mapOfEdges[edge] = line
            line.stroke = Color.BLACK
            line.strokeWidth = 5.0
            line.isVisible = false
            this.add(line)
        }
        _mapOfEdges
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


    private fun Polygon.findTileId(): Int = listOfTiles.indexOf(this)
    private fun Circle.findId(): Int = listOfCircles.indexOf(this)
    private fun Text.findId(): Int = listOfLabels.indexOf(this)
    private fun Polygon.findSailId(): Int = listOfSails.indexOf(this)
    private fun Polygon.findBoatId(): Int = this.id.takeLast(1).toInt()

    init {
        listOfTiles.withEach {
            val tile = board[findTileId()]
            val styleName = tile.type.name.lowercase()
            addClass(styleName)
            setOnMouseClicked {
                if (tile.type != Tile.Type.Desert) {
                    TileFragment(tile).openModal()
                }
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
        listOfBoats.withEach {
            setOnMouseClicked {
                val port = board.ports[findBoatId()]
                PortFragment(port).openModal()
            }
        }
        listOfSails.withEach {
            setOnMouseClicked {
                val port = board.ports[findSailId()]
                PortFragment(port).openModal()
            }
        }
    }
}