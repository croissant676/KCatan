/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui.init

import dev.kason.catan.core.board.Edge
import dev.kason.catan.core.board.Vertex
import dev.kason.catan.core.game
import dev.kason.catan.core.player.Player
import dev.kason.catan.core.player.plusAssign
import dev.kason.catan.ui.BoardBottomView
import dev.kason.catan.ui.GameView
import dev.kason.catan.ui.board.*
import dev.kason.catan.ui.side.BaseSidePanel
import dev.kason.catan.ui.side.PlayerResourceCosts
import javafx.scene.Parent
import javafx.scene.control.Button
import tornadofx.*
import kotlin.properties.Delegates

val order: List<Player> by lazy {
    game.players + game.players.reversed()
}
var orderIndex = 0
    set(value) {
        field = value
        game.currentPlayerIndex = if (orderIndex >= game.players.size) {
            val minus = orderIndex - game.players.size + 1
            game.players.size - minus
        } else orderIndex
    }

class InitSettlementConstructionPanel(
    private val settlementSelectionFragment: SettlementSelectionFragment,
    val player: Player
) : Fragment() {
    private val gameView: GameView by inject()
    private var currentVertex: Vertex by Delegates.notNull()
    override val root: Parent = borderpane {
        top {
            add(PlayerResourceCosts(player.resources))
        }
        bottom {
            add(SettlementConstructionFragment {
                currentVertex.player = player
                player.settlements += currentVertex
                if (orderIndex >= game.players.size) currentVertex.tiles.values.forEach {
                    it.type.resource?.let { resourceType ->
                        player.resources += resourceType
                    }
                }
                val roadSelectionFragment = RoadSelectionFragment(player, edges = currentVertex.edges)
                gameView.boardPanel = roadSelectionFragment
                gameView.sidePanel = InitRoadConstructionPanel(roadSelectionFragment, player)
            }.also {
                with(it.buildButton) {
                    isDisable = true
                    settlementSelectionFragment.block = { vertex ->
                        isDisable = false
                        currentVertex = vertex
                    }
                }
            })
        }
    }

    class SettlementConstructionFragment(block: () -> Unit) : Fragment() {
        override val root: Parent by fxml("/fxml/game_init_settlement.fxml")
        internal val buildButton: Button by fxid()

        init {
            buildButton.action(block)
        }
    }
}

class InitRoadConstructionPanel(
    private val roadSelectionFragment: RoadSelectionFragment,
    val player: Player
) : Fragment() {
    private val gameView: GameView by inject()
    private var currentEdge: Edge by Delegates.notNull()
    override val root: Parent = borderpane {
        top {
            add(PlayerResourceCosts(player.resources))
        }
        bottom {
            add(RoadConstructionFragment {
                currentEdge.player = player
                player.roads += currentEdge
                if (game.currentPlayerIndex > game.players.size) {
                    gameView.sidePanel = BaseSidePanel(game.nextPlayer())
                    gameView.boardPanel = BoardView(game.board)
                } else {
                    orderIndex++
                    if (game.currentPlayerIndex < 0) {
                        game.currentPlayerIndex = 0
                        gameView.sidePanel = BaseSidePanel(game.currentPlayer)
                        gameView.boardPanel = BoardView(game.board)
                        gameView.boardBottomView = BoardBottomView(game, gameView)
                        return@RoadConstructionFragment
                    }
                    gameView.replaceWith(InitNextPlayerView(game.currentPlayer) {
                        val settlementSelectionFragment =
                            SettlementSelectionFragment(settlements = game.getPossibleSettlementsInit())
                        gameView.boardPanel = settlementSelectionFragment
                        gameView.sidePanel =
                            InitSettlementConstructionPanel(settlementSelectionFragment, game.currentPlayer)
                        (gameView.boardBottomView as InitBottomView).updateCurrentPlayer()
                    }, ViewTransition.Fade(0.4.seconds))
                }
            }.also {
                with(it.buildButton) {
                    isDisable = true
                    roadSelectionFragment.block = { edge ->
                        isDisable = false
                        currentEdge = edge
                    }
                }
            })
        }
    }

    class RoadConstructionFragment(block: () -> Unit) : Fragment() {
        override val root: Parent by fxml("/fxml/game_init_roads.fxml")
        internal val buildButton: Button by fxid()

        init {
            buildButton.action(block)
        }
    }
}