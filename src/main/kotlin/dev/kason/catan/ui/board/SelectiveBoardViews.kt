/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui.board

import dev.kason.catan.core.Constants
import dev.kason.catan.core.board.*
import dev.kason.catan.core.game
import dev.kason.catan.core.player.Player
import javafx.scene.Parent
import mu.KLogging
import tornadofx.*

class RoadSelectionFragment(
    player: Player = game.currentPlayer,
    board: Board = game.board,
    edges: List<Edge> = game.getPossibleRoads(player)
) : BoardView(board) {
    companion object : KLogging()

    lateinit var block: (Edge) -> Unit

    init {
        for (edge in edges) {
            mapOfEdges[edge]!!.apply {
                addClass("board-selection-line")
                setOnMouseClicked {
                    edges.map { mapOfEdges[it]!! }.withEach {
                        addClass("board-unselected-line")
                        removeClass("board-selection-line")
                    }
                    removeClass("board-unselected-line")
                    addClass("board-selection-line")
                    block(edge)
                }
                isVisible = true
            }
        }
    }
}

class SettlementSelectionFragment(
    player: Player = game.currentPlayer,
    board: Board = game.board,
    settlements: List<Vertex> = game.getPossibleSettlements(player)
) : BoardView(board) {
    companion object : KLogging()

    lateinit var block: (Vertex) -> Unit

    init {
        for (vertex in game.getPossibleSettlements(player)) {
            mapOfVertices[vertex]!!.apply {
                addClass("board-selection-item")
                setOnMouseClicked {
                    settlements.map { mapOfVertices[it]!! }.withEach {
                        removeClass("board-selection-item")
                        addClass("board-unselected-item")
                    }
                    removeClass("board-unselected-item")
                    addClass("board-selection-item")
                    block(vertex)
                }
                isVisible = true
                radius = Constants.settlementRadius
            }
        }
    }
}

class CitySelectionFragment(player: Player, board: Board) : BoardView(board) {
    lateinit var block: (Vertex) -> Unit

    init {
        val settlements = player.settlements.filter { it.isSettlement }
        for (vertex in settlements) {
            mapOfVertices[vertex]!!.apply {
                addClass("board-selection-item")
                setOnMouseClicked {
                    settlements.map { mapOfVertices[it]!! }.withEach {
                        removeClass("board-selection-item")
                        addClass("board-unselected-item")
                    }
                    removeClass("board-unselected-item")
                    addClass("board-selection-item")
                    block(vertex)
                }
                isVisible = true
            }
        }
    }
}

class RobberSelectionFragment(board: Board) : BoardView(board) {
    override val root: Parent by fxml("/fxml/board.fxml")
    lateinit var block: (Tile) -> Unit

    init {
        listOfTiles.withEach {
            setOnMouseClicked {
                val tileId = findTileId()
                val tile = board[tileId]
                if (tile.type == Tile.Type.Desert) {
                    logger.warn("Attempted to move to desert tile")
                    return@setOnMouseClicked
                } else if (board.robberIndex == tileId) {
                    logger.warn("Attempted to move to same tile")
                    return@setOnMouseClicked
                }
                updateRobber(tile)
                block(tile)
            }
        }
    }
}