/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.catan
import dev.kason.catan.core.Game
import dev.kason.catan.core.game
import dev.kason.catan.ui.GameCreationSettings.seedIntegerProperty
import java.time.LocalDate
import javafx.beans.property.*
import javafx.scene.Parent
import javafx.scene.control.*
import mu.KLogging
import tornadofx.*


object GameCreationSettings {
    val gameNameStringProperty = SimpleStringProperty("Game_${LocalDate.now()}")
    var gameName by gameNameStringProperty
    var numberOfPlayers = -1
    var seedIntegerProperty : IntegerProperty = SimpleIntegerProperty(0)
    var seed by seedIntegerProperty
}

class UserCountSettingView: View(catan("Creation")) {
    companion object: KLogging()
    override val root: Parent by fxml("/fxml/user_count_setting.fxml")
    private val playerComboBox: ComboBox<String> by fxid()
    private val nextButton: Button by fxid()
    private val gameNameField: TextField by fxid()
    private val seedSpinner: Spinner<Int> by fxid()
    init {
        playerComboBox.items.addAll("2 Players", "3 Players", "4 Players")
        playerComboBox.selectionModel.select(0)
        gameNameField.apply {
            isEditable = true
            textProperty().bind(GameCreationSettings.gameNameStringProperty)
        }
        seedSpinner.apply {
            valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Int.MAX_VALUE, 0)
            val objectProp: ObjectProperty<Int> = SimpleObjectProperty(0)
            seedIntegerProperty = IntegerProperty.integerProperty(objectProp)
            valueFactory.valueProperty().bindBidirectional(objectProp)
        }
        nextButton.action {
            GameCreationSettings.numberOfPlayers = playerComboBox.selectionModel.selectedIndex + 2
            logger.info { "Selected ${playerComboBox.selectionModel.selectedItem} with name ${GameCreationSettings.gameName}" }
            game = Game.createGameFromSettings()
            replaceWith<GameView>()
        }
    }
}