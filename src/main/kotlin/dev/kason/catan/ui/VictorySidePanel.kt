/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.core.player.Player
import javafx.scene.Parent
import javafx.scene.shape.Circle
import javafx.scene.text.Text
import tornadofx.Fragment

class VictorySidePanel(player: Player) : Fragment() {
    override val root: Parent by fxml("/fxml/victory_side_panel.fxml")
    private val playerWonLabel: Text by fxid()
    private val playerCircle: Circle by fxid()
    init {
        playerWonLabel.text = "Player ${player.id} won!"
        playerCircle.fill = player.color.jfxColor
    }
}
