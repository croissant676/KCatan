/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.catan
import dev.kason.catan.catanAlert
import dev.kason.catan.core.*
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Polygon
import mu.KLogging
import tornadofx.*
import kotlin.system.exitProcess

class GameView : View(catan("Game")) {
    private val sidePanelViewProperty = SimpleObjectProperty<View>(BaseSidePanel(game.currentPlayer))
    var sidePanel by sidePanelViewProperty
    override val root: Parent = borderpane {
        left {
            add(BoardView(game.board))
        }
        sidePanelViewProperty.addListener { _, oldValue, newValue ->
            oldValue.replaceWith(newValue, ViewTransition.Slide(0.5.seconds))
        }
        right { add(sidePanelViewProperty.value) }
        bottom {
            add(BoardBottomView(game, this@GameView))
        }
        primaryStage.width = 1215.0
        primaryStage.height = 720.0
    }
}

class BoardBottomView(val game: Game, val gameView: GameView): View() {
    companion object : KLogging()

    override val root: Parent by fxml("/fxml/board_bottom.fxml")
    private val rollButton: Button by fxid()
    private val passButton: Button by fxid()
    private val exitButton: Button by fxid()
    private val exitSaveButton: Button by fxid()
    private val sum: Label by fxid()
    private val players = List(4) { fxmlLoader.namespace["player$it"] as Circle }
    private val playerIndicators = List(4) { fxmlLoader.namespace["playerIndicator$it"] as Polygon }
    private val leftDice = List(7) { fxmlLoader.namespace["leftDice$it"] as Circle }
    private val rightDice = List(7) { fxmlLoader.namespace["rightDice$it"] as Circle }

    private val mapOfResults = mapOf(
        1 to arrayOf(3),
        2 to arrayOf(0, 6),
        3 to arrayOf(0, 3, 6),
        4 to arrayOf(0, 2, 4, 6),
        5 to arrayOf(0, 2, 3, 4, 6),
        6 to arrayOf(0, 1, 2, 4, 5, 6)
    )

    init {
        players.forEachIndexed { index, circle ->
            if (index >= game.players.size) {
                circle.isVisible = false
            } else {
                circle.fill = game.players[index].color.jfxColor
            }
        }
        updateDice()
        exitButton.action {
            exitProcess(0)
        }
        exitSaveButton.action {
            exitProcess(0)
        }
        rollButton.apply {
            action {
                game.generateRoll()
                updateDice()
                isDisable = true
            }
        }
        passButton.apply {
            action {
                if (game.currentTurn.rolledDice) {
                    rollButton.isDisable = false
                    gameView.replaceWith(
                        NextPlayerView(game.nextPlayer())
                    )
                    updateCurrentPlayer()
                    gameView.sidePanel = BaseSidePanel(game.currentPlayer)
                } else {
                    catanAlert(
                        header = "You have not rolled the dice",
                        content = "You must roll the dice before you can pass."
                    )
                }
            }
        }
        updateCurrentPlayer()
    }

    private fun makeDiceInvisible() {
        leftDice.forEach { it.isVisible = false }
        rightDice.forEach { it.isVisible = false }
    }

    fun updateDice(roll: RollResults =  game.roll) {
        makeDiceInvisible()
        sum.text = "Sum: ${roll.sum()}"
        mapOfResults[roll.first]?.also { logger.debug { "Displaying left dots ${it.contentToString()}" } }?.forEach {
            leftDice[it].isVisible = true
        } ?: logger.warn { "Roll left is not valid: ${roll.first}" }
        mapOfResults[roll.second]?.also { logger.debug { "Displaying right dots ${it.contentToString()}" } }?.forEach {
            rightDice[it].isVisible = true
        } ?: logger.warn { "Roll right is not valid: ${roll.second}" }
    }

    fun updateCurrentPlayer() {
        playerIndicators.forEachIndexed { index, polygon ->
            if (index == game.currentPlayerIndex) {
                polygon.fill = c("#b3b3b3")
            } else {
                polygon.fill = Color.TRANSPARENT
            }
        }
    }

    override fun onDock() {
    }
}