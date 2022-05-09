/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui.side

import dev.kason.catan.core.board.Tile
import dev.kason.catan.core.board.Vertex
import dev.kason.catan.core.game
import dev.kason.catan.core.player.Player
import dev.kason.catan.ui.GameView
import dev.kason.catan.ui.board.*
import java.util.Collections
import javafx.scene.Parent
import javafx.scene.control.Button
import tornadofx.Fragment
import tornadofx.action
import kotlin.properties.Delegates

class RobberSideTileSelection(
    private val robberSelectionFragment: RobberSelectionFragment,
    val player: Player
) : Fragment() {
    private val gameView: GameView by inject()
    private var currentTile: Tile by Delegates.notNull()
    override val root: Parent by fxml("/fxml/robber_tile_side.fxml")
    private val selectButton: Button by fxid()

    init {
        with(selectButton) {
            isDisable = true
            robberSelectionFragment.block = { tile ->
                isDisable = false
                currentTile = tile
            }
            action {
                game.board.robberIndex = currentTile.id
                val vertices = currentTile.vertices.values.filter { it.isSettlement && it.player != player }
                val settlementSelectionFragment = SettlementSelectionFragment(settlements = vertices)
                gameView.boardPanel = settlementSelectionFragment
                gameView.sidePanel = RobberSideSettlementSelection(settlementSelectionFragment, player)
            }
        }
    }
}

class RobberSideSettlementSelection(
    private val settlementSelectionFragment: SettlementSelectionFragment,
    val player: Player
) : Fragment() {
    private val gameView: GameView by inject()
    private var currentVertex: Vertex by Delegates.notNull()
    override val root: Parent by fxml("/fxml/robber_settlement_side.fxml")
    private val selectButton: Button by fxid()

    init {
        with(selectButton) {
            isDisable = true
            settlementSelectionFragment.block = { vertex ->
                isDisable = false
                currentVertex = vertex
            }
            action {
                val stolenPlayer = currentVertex.player!!
                if (stolenPlayer.resources.any { it.value != 0 }) {
                    val list =
                        stolenPlayer.resources.flatMap { (resource, value) -> Collections.nCopies(value, resource) }
                    val resource = list.random(game.random)
                    stolenPlayer.resources[resource] = stolenPlayer.resources[resource]!! - 1
                    player.resources[resource] = player.resources[resource]!! + 1
                }
                gameView.boardPanel = BoardView(game.board)
                gameView.sidePanel = BaseSidePanel(player)
                gameView.enableButtons()
            }
        }
    }
}