/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.catan
import javafx.scene.Parent
import javafx.scene.layout.AnchorPane
import javafx.scene.control.Button
import mu.KLogging
import tornadofx.*

class MenuView: View(catan("Menu")) {
    companion object: KLogging()
    override val root: AnchorPane by fxml("/fxml/menu.fxml")
    private val startButton: Button by fxid()
    private val continueButton: Button by fxid()
    private val ruleButton: Button by fxid()
    override fun onDock() {
        startButton.setOnAction {
            replaceWith<UserCountSettingView>(ViewTransition.Fade(0.5.seconds))
        }
        continueButton.setOnAction {
            logger.info("Continue button clicked")
        }
        ruleButton.setOnAction {
            logger.info("Rule button clicked")
        }
    }
}

class RulesView: View(catan("Rules")) {
    override val root: Parent by fxml("fxml/rules.fxml")
}