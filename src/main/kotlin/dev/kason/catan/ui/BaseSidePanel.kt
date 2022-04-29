/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.catanAlert
import dev.kason.catan.core.Constants
import dev.kason.catan.core.player.*
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.text.Text
import mu.KLogging
import tornadofx.*
import kotlin.random.Random

class BaseSidePanel(val player: Player) : View() {
    override val root: Parent = borderpane {
        top { add(costView(player.color.jfxColor)) }
        center { add(PlayerResourceCosts(player.resources)) }
        bottom { add(BaseSidePanelBottom(player)) }
    }
}

class BaseSidePanelBottom(val player: Player) : View() {
    companion object: KLogging()
    override val root: Parent by fxml("/fxml/base_side.fxml")
    private val buildCity: Button by fxid()
    private val buildConstruction: Button by fxid()
    private val devCardBuyButton: Button by fxid()
    private val maritimeTradeButton: Button by fxid()
    private val devCardUseButton: Button by fxid()
    private val defaultTradeButton: Button by fxid()
    private val othersTradeButton: Button by fxid()
    init {
        buildCity.action {
            logger.debug { "Attempted to build a city: $player :: ${player.resources}" }
            if (player.resources doesNotHave Constants.cityCost) {
                catanAlert(
                    header = "Not enough resources",
                    content = "You do not have enough resources to build a city."
                )
            }
        }
        devCardBuyButton.action {
            logger.debug { "Attempted to buy a dev card: $player :: ${player.resources}" }
            if (player.resources doesNotHave Constants.developmentCardCost) {
                catanAlert(
                    header = "Not enough resources",
                    content = "You do not have enough resources to buy a development card."
                )
            }
        }
    }
}

class CostsView(color: Color) : Fragment() {
    override val root: AnchorPane by fxml("/fxml/costs.fxml")
    private val roadLine: Line by fxid()
    private val settlementCircle: Circle by fxid()
    private val cityCircle: Circle by fxid()
    private val rootPane: AnchorPane by fxid()

    init {
        roadLine.stroke = color
        settlementCircle.fill = color
        cityCircle.fill = color
        rootPane.style = "-fx-background-color: ${color.css};"
    }
}

private val costsViewMap = mutableMapOf<Color, CostsView>()
fun costView(color: Color): CostsView {
    return costsViewMap.getOrPut(color) { CostsView(color) }
}

class PlayerResourceCosts(playerResources: PlayerResourceMap): Fragment() {
    override val root: Parent by fxml("/fxml/player_resources.fxml")
    private val hillResources: Text by fxid()
    private val forestResources: Text by fxid()
    private val pastureResources: Text by fxid()
    private val mountainResources: Text by fxid()
    private val fieldResources: Text by fxid()
    private val totalResources: Text by fxid()
    init {
        hillResources.text = playerResources.brick.toString()
        forestResources.text = playerResources.lumber.toString()
        pastureResources.text = playerResources.wool.toString()
        mountainResources.text = playerResources.ore.toString()
        fieldResources.text = playerResources.grain.toString()
        val total = playerResources.resources.values.sum()
        totalResources.text = total.toString()
        if(total > 7) {
            totalResources.fill = c("#ff002d")
        }
    }
}