/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.catan
import dev.kason.catan.core.Constants
import dev.kason.catan.core.board.*
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.shape.*
import javafx.scene.text.*
import mu.KLogging
import tornadofx.*

@Suppress("LocalVariableName", "MemberVisibilityCanBePrivate")
open class BoardView(
    protected val board: Board
) : View(catan("Board")) {
    companion object : KLogging()

    override val root: Parent by fxml("/fxml/board.fxml")

    protected val listOfTiles = List(19) {
        fxmlLoader.namespace["tile$it"] as Polygon
    }

    protected val listOfCircles = List(19) {
        fxmlLoader.namespace["circle$it"] as Circle
    }

    protected val listOfLabels = List(19) {
        fxmlLoader.namespace["label$it"] as Text
    }

    protected val listOfBoats = List(9) {
        fxmlLoader.namespace["boat$it"] as Polygon
    }

    protected val listOfSails = List(9) {
        fxmlLoader.namespace["sail$it"] as Polygon
    }

    protected val mapOfEdges: Map<Edge, Line> = run {
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

    protected val mapOfVertices: Map<Vertex, Circle> = run {
        val _mapOfVertices = mutableMapOf<Vertex, Circle>()
        for (vertex in board.vertices) {
            _mapOfVertices[vertex] = vertex.tiles.values.first().run {
                val location = vertices.keys.first { vertices[it] == vertex }
                val tileHexagon = listOfTiles[id]
                val translationValue = Constants.pointTranslations[location]!!
                val circle = Circle(
                    tileHexagon.layoutX + translationValue.x,
                    tileHexagon.layoutY + translationValue.y,
                    Constants.cityRadius
                )
                circle.isVisible = false
                circle.stroke = c("#b3b3b3")
                circle.strokeWidth = 3.0
                this@BoardView.add(circle)
                circle
            }
        }
        _mapOfVertices
    }

    protected fun Polygon.findTileId(): Int = listOfTiles.indexOf(this)
    protected fun Circle.findId(): Int = listOfCircles.indexOf(this)
    protected fun Text.findId(): Int = listOfLabels.indexOf(this)
    protected fun Polygon.findSailId(): Int = listOfSails.indexOf(this)
    protected fun Polygon.findBoatId(): Int = this.id.takeLast(1).toInt()

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
        updateConstructions()
    }
}