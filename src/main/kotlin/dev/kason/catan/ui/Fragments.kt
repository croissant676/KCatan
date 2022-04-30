/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.core.board.Port
import dev.kason.catan.core.board.Tile
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.shape.Circle
import mu.KLogging
import tornadofx.Fragment
import tornadofx.addClass

class TileFragment(val tile: Tile) : Fragment("Tile #${tile.id}") {
    companion object : KLogging()

    override val root: Parent by fxml("/fxml/tile_info.fxml")
    private val tileIdLabel: Label by fxid()
    private val tileTypeLabel: Label by fxid()
    private val tileValueLabel: Label by fxid()
    private val chanceLabel: Label by fxid()
    private val colorCircle: Circle by fxid()

    override fun onDock() {
        val styleName = tile.type.name.lowercase()
        logger.debug { "Displaying info panel for $tile" }
        colorCircle.addClass(styleName)
        tileIdLabel.text = "Tile #${tile.id + 1}"
        tileValueLabel.text = "Value: ${tile.value}"
        chanceLabel.text = "Chance: ${tile.chance} / 36"
        tileTypeLabel.text = "Type: ${tile.type.name}"
    }
}

class PortFragment(
    val port: Port
): Fragment("Port - ${port.formalName}") {
    companion object : KLogging()

    override val root: Parent by fxml("/fxml/port_info.fxml")
    private val portIdLabel: Label by fxid()
    private val portTypeLabel: Label by fxid()
    private val portDescLabel: Label by fxid()
    private val resourceCircle: Circle by fxid()

    override fun onDock() {
        val styleName = port.cssName
        logger.debug { "Displaying info panel for $port" }
        portIdLabel.text = "Port #${port.id + 1}"
        portTypeLabel.text = "Port ${port.formalName}"
        portDescLabel.isWrapText = true
        portDescLabel.text = port.description
        resourceCircle.addClass(styleName)
    }
}