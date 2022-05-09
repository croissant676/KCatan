/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui.dev

import dev.kason.catan.core.game
import dev.kason.catan.core.player.Player
import dev.kason.catan.core.player.ResourceType
import dev.kason.catan.ui.GameView
import dev.kason.catan.ui.board.BoardView
import dev.kason.catan.ui.side.BaseSidePanel
import dev.kason.catan.ui.trade.addResourceTypes
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import tornadofx.*

class MonopolySelectFragment(player: Player) : Fragment() {
    override val root: Parent by fxml("/fxml/monopoly_select.fxml")
    private val resourceComboBox: ComboBox<String> by fxid()
    private val tradeButton: Button by fxid()
    private val gameView: GameView by inject()

    init {
        resourceComboBox.addResourceTypes()
        resourceComboBox.selectionModel.select(0)
        tradeButton.action {
            val resource = ResourceType.valueOf(resourceComboBox.selectionModel.selectedItem!!)
            game.players.filter { it != player }.withEach {
                val number = resources[resource]!!
                resources[resource] = 0
                player.resources[resource] = player.resources[resource]!! + number
            }
            gameView.boardPanel = BoardView(game.board)
            gameView.sidePanel = BaseSidePanel(player)
            close()
        }
    }
}