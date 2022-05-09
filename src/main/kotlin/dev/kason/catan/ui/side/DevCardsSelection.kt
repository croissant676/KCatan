/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui.side

import dev.kason.catan.core.board.Edge
import dev.kason.catan.core.game
import dev.kason.catan.core.player.Player
import dev.kason.catan.ui.GameView
import dev.kason.catan.ui.board.RoadSelectionFragment
import javafx.scene.Parent
import javafx.scene.control.Button
import tornadofx.*
import kotlin.properties.Delegates

class DevCardsBuildRoadPanel(
    private val roadSelectionFragment: RoadSelectionFragment,
    val player: Player,
): Fragment() {
    private val gameView: GameView by inject()
    private var currentEdge: Edge by Delegates.notNull()
    override val root: Parent = borderpane {
        top {
            add(PlayerResourceCosts(player.resources))
        }
        bottom {
            var usedAlready = false
            add(RoadConstructionFragment {
                currentEdge.player = player
                player.roads += currentEdge
                if (usedAlready) {
                    val roadSelectionFragment = RoadSelectionFragment(player, game.board, game.getPossibleRoads(player))
                    gameView.boardPanel = roadSelectionFragment
                    gameView.sidePanel = RoadConstructionPanel(roadSelectionFragment, player, costsResources = false)
                } else {
                    usedAlready = true
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
        override val root: Parent by fxml("/fxml/road_construction.fxml")
        internal val buildButton: Button by fxid()
        init {
            buildButton.action(block)
        }
    }
}