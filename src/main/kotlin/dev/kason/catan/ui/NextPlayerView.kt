/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.catan
import dev.kason.catan.core.Game
import dev.kason.catan.core.player.Player
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import mu.KLogging
import tornadofx.View
import tornadofx.action

class NextPlayerView(val player: Player) : View(catan("Next Player")) {
    companion object : KLogging()

    override val root: Parent by fxml("/fxml/player_turn.fxml")
    private val nextPlayerLabel: Label by fxid()
    private val continueButton: Button by fxid()
    private val exitButton: Button by fxid()

    init {
        nextPlayerLabel.text = "Next Player: ${player.color.name}"
        continueButton.action {
            logger.info { "Next player button clicked!" }
            replaceWith<GameView>()
        }
        exitButton.action {
            logger.info { "Closing application" }
        }
    }

    override fun onDock() {
        primaryStage.sizeToScene()
    }
}