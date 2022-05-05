/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.catan
import dev.kason.catan.core.player.Player
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.shape.Circle
import mu.KLogging
import tornadofx.*
import kotlin.system.exitProcess

class NextPlayerView(val player: Player) : View(catan("Next Player")) {
    companion object : KLogging()

    override val root: Parent by fxml("/fxml/player_turn.fxml")
    private val nextPlayerLabel: Label by fxid()
    private val continueButton: Button by fxid()
    private val exitButton: Button by fxid()
    private val playerCircle: Circle by fxid()

    init {
        nextPlayerLabel.text = "Player ${player.color.name}"
        continueButton.action {
            logger.info { "Next player button clicked!" }
            replaceWith<GameView>(ViewTransition.Fade(0.5.seconds))
        }
        exitButton.action {
            logger.info { "Closing application" }
            exitProcess(0)
        }
        playerCircle.fill = player.color.jfxColor
    }

    override fun onDock() {
        primaryStage.sizeToScene()
    }
}