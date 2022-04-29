/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.catan
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import mu.KLogging
import tornadofx.View
import tornadofx.action

object GameCreationSettings {
    var gameName = "Default Game"
    var numberOfPlayers = -1
}

class UserCountSettingView: View(catan("Creation")) {
    companion object: KLogging()
    override val root: Parent by fxml("/fxml/user_count_setting.fxml")
    private val playerComboBox: ComboBox<String> by fxid()
    private val nextButton: Button by fxid()
    init {
        playerComboBox.items.addAll("2 Players", "3 Players", "4 Players")
        playerComboBox.selectionModel.select(0)
        nextButton.action {
            logger.info { "Selected ${playerComboBox.selectionModel.selectedItem}" }
        }
    }

}