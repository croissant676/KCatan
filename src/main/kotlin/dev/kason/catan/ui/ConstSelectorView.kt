/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.catan
import javafx.scene.Parent
import javafx.scene.control.Button
import mu.KLogging
import tornadofx.View
import tornadofx.action

class ConstSelectorView: View(catan("Construction Selector")) {
    companion object: KLogging()
    val boardView: BoardView by inject()
    override val root: Parent by fxml("/fxml/construction_selector.fxml")
    private val roadButton: Button by fxid()
    private val settlementButton: Button by fxid()
    init {
        roadButton.action {
            logger.info { "Road button clicked" }
        }
        settlementButton.action {
            logger.info { "Settlement button clicked" }
        }
    }
}