/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.catan
import dev.kason.catan.catanAlert
import dev.kason.catan.core.Constants
import dev.kason.catan.core.board.Edge
import dev.kason.catan.core.board.Vertex
import dev.kason.catan.core.game
import dev.kason.catan.core.player.*
import javafx.scene.Parent
import javafx.scene.control.Button
import mu.KLogging
import tornadofx.*
import kotlin.properties.Delegates

class ConstSelectorView(player: Player) : Fragment(catan("Construction Selector")) {
    companion object : KLogging()
    override val root: Parent by fxml("/fxml/construction_selector.fxml")
    private val roadButton: Button by fxid()
    private val settlementButton: Button by fxid()
    private val gameView: GameView by inject()
    init {
        roadButton.action {
            if (player.resources.doesNotHave(Constants.roadCost)) {
                catanAlert(
                    "Insufficient Resources",
                    "You do not have enough resources to build a road."
                )
                return@action
            }
            val roadSelectionFragment = RoadSelectionFragment(player, game.board)
            gameView.boardPanel = roadSelectionFragment
            gameView.sidePanel = RoadConstructionPanel(roadSelectionFragment, player)
        }
        settlementButton.action {
            logger.info { "Settlement button clicked" }
            if (player.resources doesNotHave Constants.settlementCost) {
                catanAlert(
                    "Insufficient Resources",
                    "You do not have enough resources to build a settlement."
                )
                return@action
            }
            val settlementSelectionFragment = SettlementSelectionFragment(player, game.board)
            gameView.boardPanel = settlementSelectionFragment
            gameView.sidePanel = SettlementConstructionPanel(settlementSelectionFragment, player)
        }
    }
}

class CityConstructionPanel(
    private val citySelectionFragment: CitySelectionFragment,
    val player: Player
): Fragment() {
    private val gameView: GameView by inject()
    private var currentVertex: Vertex by Delegates.notNull()
    override val root: Parent = borderpane {
        top {
            add(PlayerResourceCosts(player.resources))
        }
        bottom {
            add(CityConstructionFragment {
                currentVertex.isCity = true
                player.resources -= Constants.cityCost
                gameView.boardPanel = BoardView(game.board)
                gameView.sidePanel = BaseSidePanel(player)
                // Reset both the board and the side panel
            }.apply {
                with(upgradeButton) {
                    isDisable = true
                    citySelectionFragment.block = { vertex ->
                        isDisable = false
                        currentVertex = vertex
                    }
                }
            })
        }
    }

    class CityConstructionFragment(block: () -> Unit) : Fragment() {
        override val root: Parent by fxml("/fxml/city_construction.fxml")
        internal val upgradeButton: Button by fxid()

        init {
            upgradeButton.action(block)
        }
    }
}

class SettlementConstructionPanel(
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
                player.resources -= Constants.settlementCost
                gameView.boardPanel = BoardView(game.board)
                gameView.sidePanel = BaseSidePanel(player)
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
        override val root: Parent by fxml("/fxml/settlement_construction.fxml")
        internal val buildButton: Button by fxid()

        init {
            buildButton.action(block)
        }
    }
}

class RoadConstructionPanel(
    private val roadSelectionFragment: RoadSelectionFragment,
    val player: Player
): Fragment() {
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
                player.resources -= Constants.roadCost
                gameView.boardPanel = BoardView(game.board)
                gameView.sidePanel = BaseSidePanel(player)
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
        override val root: Parent by fxml("/fxml/road_construction.fxml")
        internal val buildButton: Button by fxid()

        init {
            buildButton.action(block)
        }
    }
}