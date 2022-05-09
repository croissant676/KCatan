/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.catan
import dev.kason.catan.catanAlert
import dev.kason.catan.core.*
import dev.kason.catan.file.exitAndSave
import dev.kason.catan.ui.board.BoardView
import dev.kason.catan.ui.board.RobberSelectionFragment
import dev.kason.catan.ui.init.InitBottomView
import dev.kason.catan.ui.side.*
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Polygon
import mu.KLogging
import tornadofx.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class GameView : View(catan("Game")) {
    companion object : KLogging()
    private val sidePanelViewProperty = SimpleObjectProperty<UIComponent>(BaseSidePanel(game.currentPlayer))
    var sidePanel: UIComponent by sidePanelViewProperty
    private val boardViewProperty = SimpleObjectProperty(BoardView(game.board))
    var boardPanel: BoardView by boardViewProperty
    private val boardBottomViewProperty = SimpleObjectProperty<UIComponent>(InitBottomView())
    var boardBottomView: UIComponent by boardBottomViewProperty
    override val root: Parent = borderpane {
        boardViewProperty.addListener { _, oldValue, newValue ->
            oldValue.replaceWith(newValue, ViewTransition.Fade(0.3.seconds))
        }
        left {
            add(boardPanel)
        }
        sidePanelViewProperty.addListener { _, oldValue, newValue ->
            oldValue.replaceWith(newValue, ViewTransition.Fade(0.3.seconds))
        }
        right { add(sidePanelViewProperty.value) }
        boardBottomViewProperty.addListener { _, oldValue, newValue ->
            oldValue.replaceWith(newValue, ViewTransition.Fade(0.3.seconds))
        }
        bottom {
            add(boardBottomView)
        }
    }

    fun onRollOfSeven() {
        discardExtraResources()
        val robberPanel = RobberSelectionFragment(game.board)
        boardPanel = robberPanel
        sidePanel = RobberSideTileSelection(robberPanel, game.currentPlayer)
        val boardBottomView = boardBottomView as BoardBottomView
        boardBottomView.apply {
            rollButton.isDisable = true
            passButton.isDisable = true
            backButton.isDisable = true
        }
    }

    private fun discardExtraResources() {
        val extraResourcePlayers = game.players.filter { it.resources.values.sum() >= 7 }
        if (extraResourcePlayers.isEmpty()) {
            return
        }
        val arrayDeque = ArrayDeque(extraResourcePlayers)
        sidePanel = DiscardFragment(arrayDeque.removeFirst(), arrayDeque)
    }

    fun enableButtons() {
        val boardBottomView = boardBottomView as BoardBottomView
        boardBottomView.apply {
            rollButton.isDisable = false
            passButton.isDisable = false
            backButton.isDisable = false
        }
    }

    fun checkPlayerVictory() {
        val player = game.checkPlayerVictory()
        if (player != null) {
            sidePanel = VictorySidePanel(player)
        }
    }
}

class BoardBottomView(val game: Game, private val gameView: GameView): View() {
    companion object : KLogging()

    override val root: Parent by fxml("/fxml/board_bottom.fxml")
    val rollButton: Button by fxid()
    val passButton: Button by fxid()
    private val exitButton: Button by fxid()
    private val exitSaveButton: Button by fxid()
    private val sum: Label by fxid()
    private val players = List(4) { fxmlLoader.namespace["player$it"] as Circle }
    private val playerIndicators = List(4) { fxmlLoader.namespace["playerIndicator$it"] as Polygon }
    private val leftDice = List(7) { fxmlLoader.namespace["leftDice$it"] as Circle }
    private val rightDice = List(7) { fxmlLoader.namespace["rightDice$it"] as Circle }
    val backButton: Button by fxid()

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
            exitAndSave()
        }
        rollButton.apply {
            action {
                val shouldRunRobber = game.generateRoll()
                updateDice()
                if (shouldRunRobber) gameView.onRollOfSeven()
                else if (gameView.sidePanel is BaseSidePanel) gameView.sidePanel = BaseSidePanel(game.currentPlayer)
                isDisable = true
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
                                "You have not rolled the dice",
                                "You must roll the dice before you can pass."
                            )
                        }
                    }
                }
                backButton.action {
                    logger.info { "Back button pressed, switching to a base side panel" }
                    gameView.sidePanel = BaseSidePanel(game.currentPlayer)
                    gameView.boardPanel = BoardView(game.board)
                    backButton.isDisable = true
                    thread {
                        Thread.sleep(500)
                        runLater {
                            backButton.isDisable = false
                        }
                    }
                }
                updateCurrentPlayer()
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

    fun updateCurrentPlayer() = playerIndicators.forEachIndexed { index, polygon ->
        if (index == game.currentPlayerIndex) {
            polygon.fill = c("#b3b3b3")
        } else {
            polygon.fill = Color.TRANSPARENT
        }
    }
}