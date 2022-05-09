/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui.dev

import dev.kason.catan.core.game
import dev.kason.catan.core.player.DevCardType
import dev.kason.catan.core.player.Player
import dev.kason.catan.ui.GameView
import dev.kason.catan.ui.board.BoardView
import dev.kason.catan.ui.side.BaseSidePanel
import dev.kason.catan.ui.side.DevCardsSidePanel
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.text.Text
import tornadofx.Fragment
import tornadofx.action

class DevCardPurchased(player: Player, devCardType: DevCardType) : Fragment("Dev Card Purchased") {
    private val gameView: GameView by inject()
    override val root: Parent by fxml("/fxml/dev_card_purchased.fxml")
    private val openCards: Button by fxid()
    private val okButton: Button by fxid()
    private val devCardText: Text by fxid()
    init {
        devCardText.text = "You got a ${devCardType.formalName} card"
        openCards.action {
            gameView.sidePanel = DevCardsSidePanel(player)
            gameView.boardPanel = BoardView(game.board)
            close()
        }
        okButton.action {
            gameView.sidePanel = BaseSidePanel(player)
            gameView.boardPanel = BoardView(game.board)
            close()
        }
    }
}