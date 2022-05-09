/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.catan
import dev.kason.catan.file.SelectionView
import java.awt.Desktop
import java.net.URI
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import mu.KLogging
import tornadofx.*

class MenuView: View(catan("Menu")) {
    companion object: KLogging()
    override val root: AnchorPane by fxml("/fxml/menu.fxml")
    private val startButton: Button by fxid()
    private val continueButton: Button by fxid()
    private val ruleButton: Button by fxid()
    init {
        startButton.action {
            replaceWith<UserCountSettingView>(ViewTransition.Fade(0.8.seconds))
        }
        continueButton.action {
            replaceWith(SelectionView(), ViewTransition.Fade(0.8.seconds))
        }
        ruleButton.action {
            replaceWith(RulesView(), ViewTransition.Fade(0.8.seconds))
        }
    }

    override fun onDock() {
        primaryStage.width = 600.0
        primaryStage.height = 440.0
    }
}

class RulesView: View(catan("Rules")) {
    override val root: Parent by fxml("/fxml/rules.fxml")
    private val openRulesButton: Button by fxid()
    private val backButton: Button by fxid()

    init {
        openRulesButton.action {
            Desktop.getDesktop().browse(URI("https://www.catan.com/sites/prod/files/2021-06/catan_base_rules_2020_200707.pdf"))
        }
        backButton.action {
            replaceWith<MenuView>(ViewTransition.Fade(0.8.seconds))
        }
        primaryStage.width = 620.0
        primaryStage.height = 581.0
    }
}