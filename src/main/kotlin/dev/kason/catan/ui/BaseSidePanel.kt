/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.core.player.*
import javafx.scene.Parent
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.text.Text
import tornadofx.*
import kotlin.random.Random

class BaseSidePanel(val player: Player) : View() {
    override val root: Parent = borderpane {
        top { add(costView(player.color.jfxColor)) }
        center { add(PlayerResourceCosts(player.resources)) }
    }
}

class BaseSidePanelBottom(val player: Player) : View() {
    override val root: Parent by fxml("/fxml/base_side.fxml")
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
        ResourceType.values().forEach {
            playerResources.resources += it to Random.nextInt(8)
        }
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