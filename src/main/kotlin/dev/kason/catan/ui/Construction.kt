/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.catan
import dev.kason.catan.catanAlert
import dev.kason.catan.core.Constants
import dev.kason.catan.core.player.Player
import dev.kason.catan.core.player.doesNotHave
import javafx.scene.Parent
import javafx.scene.control.Button
import mu.KLogging
import tornadofx.Fragment
import tornadofx.action

class ConstSelectorView(player: Player) : Fragment(catan("Construction Selector")) {
    companion object : KLogging()
    override val root: Parent by fxml("/fxml/construction_selector.fxml")
    private val roadButton: Button by fxid()
    private val settlementButton: Button by fxid()
    private val gameView: GameView by inject()
    init {
        roadButton.action {
            if (player.resources.doesNotHave(Constants.roadCost)) {
                catanAlert(
                    "Insufficient Resources",
                    "You do not have enough resources to build a road."
                )
                return@action
            }
        }
        settlementButton.action {
            logger.info { "Settlement button clicked" }
            if (player.resources doesNotHave Constants.settlementCost) {
                catanAlert(
                    "Insufficient Resources",
                    "You do not have enough resources to build a settlement."
                )
                return@action
            }
            gameView.boardPanel = SettlementSelectionFragment()
        }
    }
}

class CityConstructionPanel(
    val citySelectionFragment: CitySelectionFragment,
): Fragment() {
    override val root: Parent by fxml("/fxml/city_construction.fxml")

}