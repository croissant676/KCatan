/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.file

import dev.kason.catan.catan
import dev.kason.catan.core.game
import dev.kason.catan.ui.*
import java.io.File
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import tornadofx.*

class SelectionItem(val file: File, selectionView: SelectionView) : Fragment() {
    override val root: Parent by fxml("/fxml/previous_game_item.fxml")
    private val nameLabel: Label by fxid()
    private val text: Text by fxid()
    private val openButton: Button by fxid()
    private val deleteButton: Button by fxid()

    init {
        text.text = file.absolutePath
        nameLabel.text = file.name
        openButton.action {
            game = loadFileJson(file) ?: return@action
            primaryStage.width = 1215.0
            primaryStage.height = 720.0
            val gameView = find<GameView>()
            gameView.boardBottomView = BoardBottomView(game, gameView)
            selectionView.replaceWith(NextPlayerView(game.currentPlayer), ViewTransition.Fade(0.5.seconds))
        }
        deleteButton.action {
            file.delete()
            removeFromParent()
        }
    }
}

class SelectionView : View(catan("Select Previous Game")) {
    override val root: Parent by fxml("/fxml/open_previous.fxml")
    private val box: VBox by fxid()
    private val backButton: Button by fxid()

    init {
        val files = listAllFiles()
        files.forEach {
            box.add(SelectionItem(it, this))
        }
        backButton.action {
            replaceWith<MenuView>(ViewTransition.Fade(0.8.seconds))
        }
        primaryStage.width = 601.0
        primaryStage.height = 535.0
    }
}