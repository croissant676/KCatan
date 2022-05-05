/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui.init

import dev.kason.catan.catan
import dev.kason.catan.core.game
import dev.kason.catan.core.player.Player
import dev.kason.catan.file.exitAndSave
import dev.kason.catan.ui.GameView
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Polygon
import mu.KLogging
import tornadofx.*
import kotlin.system.exitProcess

class InitNextPlayerView(player: Player, block: () -> Unit): Fragment(catan("Next Player")) {
    companion object: KLogging()
    override val root: Parent by fxml("/fxml/player_turn.fxml")
    private val nextPlayerLabel: Label by fxid()
    private val continueButton: Button by fxid()
    private val exitButton: Button by fxid()
    private val playerCircle: Circle by fxid()

    init {
        nextPlayerLabel.text = "Player ${player.color.name}"
        continueButton.action {
            replaceWith<GameView>(ViewTransition.Fade(0.5.seconds))
            block()
        }
        exitButton.action {
            logger.info { "Closing application" }
            exitProcess(0)
        }
        playerCircle.fill = player.color.jfxColor
    }
}

class InitBottomView: View(catan("Bottom")) {
    companion object: KLogging()
    override val root: Parent by fxml("/fxml/init_bottom.fxml")
    private val exitButton: Button by fxid()
    private val exitSaveButton: Button by fxid()
    private val players = List(4) { fxmlLoader.namespace["player$it"] as Circle }
    private val playerIndicators = List(4) { fxmlLoader.namespace["playerIndicator$it"] as Polygon }
    init {
        players.forEachIndexed { index, circle ->
            if (index >= game.players.size) {
                circle.isVisible = false
            } else {
                circle.fill = game.players[index].color.jfxColor
            }
        }
        exitButton.action {
            exitProcess(0)
        }
        exitSaveButton.action {
            exitAndSave()
        }
        updateCurrentPlayer()
    }

    fun updateCurrentPlayer() = playerIndicators.forEachIndexed { index, polygon ->
        if (index == game.currentPlayerIndex) {
            polygon.fill = c("#b3b3b3")
        } else {
            polygon.fill = Color.TRANSPARENT
        }
    }
}