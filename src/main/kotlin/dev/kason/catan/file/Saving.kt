/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.file

import dev.kason.catan.core.Game
import dev.kason.catan.core.game
import java.awt.Desktop
import java.io.File
import javafx.scene.Parent
import javafx.scene.control.Button
import tornadofx.Fragment
import kotlin.system.exitProcess

fun exitAndSave(saveGame: Game = game) {
    SaveFinishedUI(saveGame.saveJson()!!).openModal()
}

class SaveFinishedUI(file: File) : Fragment("Saved game") {
    override val root: Parent by fxml("/fxml/save_finish.fxml")
    private val exitButton: Button by fxid()
    private val openFile: Button by fxid()

    init {
        exitButton.setOnAction {
            exitProcess(0)
        }
        openFile.setOnAction {
            Desktop.getDesktop().open(file)
        }
    }
}