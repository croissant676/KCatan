/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.file

import dev.kason.catan.ui.GameView
import java.io.File
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import tornadofx.*

class SelectionItem(val file: File) : Fragment() {
    override val root: Parent by fxml("/fxml/previous_game_item.fxml")
    private val nameLabel: Label by fxid()
    private val text: Text by fxid()

    init {
        text.text = file.absolutePath
        nameLabel.text = "Game ${file.name}"
    }
}

class SelectionView : View() {
    override val root: Parent by fxml("/fxml/open_selection.fxml")
    private val box: VBox by fxid()
    private val selectButton: Button by fxid()
    private val deleteButton: Button by fxid()
    private val openFileLocation: Button by fxid()
    init {
        val files = listAllFiles()
        files.forEach {
            box.add(SelectionItem(it))
        }
        selectButton.action {
            replaceWith<GameView>(ViewTransition.Fade(0.8.seconds))
        }
    }
}