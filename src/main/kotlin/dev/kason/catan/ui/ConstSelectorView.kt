/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.catan
import dev.kason.catan.catanAlert
import dev.kason.catan.core.Constants
import dev.kason.catan.core.Game
import dev.kason.catan.core.player.Player
import dev.kason.catan.core.player.doesNotHave
import javafx.scene.Parent
import javafx.scene.control.Button
import mu.KLogging
import tornadofx.View
import tornadofx.action

class ConstSelectorView(val game: Game, val player: Player): View(catan("Construction Selector")) {
    companion object: KLogging()
    val boardView: BoardView by inject()
    override val root: Parent by fxml("/fxml/construction_selector.fxml")
    private val roadButton: Button by fxid()
    private val settlementButton: Button by fxid()
    init {
        roadButton.action {
            logger.info { "Road button clicked" }
            if (player.resources doesNotHave Constants.roadCost) {
                catanAlert(
                    header = "Not enough resources",
                    content = "You do not have enough resources to build a road."
                )
            } else {
                //show build road ui
            }
        }
        settlementButton.action {
            logger.info { "Settlement button clicked" }
            if (player.resources doesNotHave Constants.settlementCost) {
                catanAlert(
                    header = "Not enough resources",
                    content = "You do not have enough resources to build a settlement."
                )
            } else {
                //show build settlement ui
            }
        }
    }
}