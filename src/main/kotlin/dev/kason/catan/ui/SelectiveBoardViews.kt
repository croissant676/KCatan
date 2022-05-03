/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.core.Constants
import dev.kason.catan.core.board.*
import dev.kason.catan.core.game
import dev.kason.catan.core.player.Player
import javafx.scene.Parent
import mu.KLogging
import tornadofx.*

class RoadSelectionFragment(player: Player, board: Board) : BoardView(board) {
    companion object : KLogging()

    lateinit var block: (Edge) -> Unit

    init {
        for (edge in game.board.edges) {
            mapOfEdges[edge]!!.apply {
                addClass("board-selection-line")
                setOnMouseClicked {
                    mapOfEdges.values.withEach {
                        if (this@apply == this) return@withEach
                        removeClass("board-selection-line")
                        addClass("board-unselected-line")
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

class SettlementSelectionFragment(player: Player, board: Board) : BoardView(board) {
    companion object : KLogging()

    lateinit var block: (Vertex) -> Unit

    init {
        for (vertex in game.getPossibleSettlements(player)) {
            mapOfVertices[vertex]!!.apply {
                addClass("board-selection-item")
                setOnMouseClicked {
                    mapOfVertices.values.withEach {
                        if (this@apply == this) return@withEach
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
        for (vertex in player.settlements.filter { it.isSettlement }) {
            mapOfVertices[vertex]!!.apply {
                addClass("board-selection-item")
                setOnMouseClicked {
                    mapOfVertices.values.withEach {
                        if (this@apply == this) return@withEach
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

class RobberSelectionFragment(val board: Board): Fragment() {
    override val root: Parent by fxml("/fxml/board.fxml")
}