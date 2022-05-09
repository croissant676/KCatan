/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui.dev

import dev.kason.catan.core.player.*
import dev.kason.catan.ui.trade.addResourceTypes
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import tornadofx.*

class YOPSelectFragment(player: Player, block: YOPSelectFragment.() -> Unit) : Fragment() {
    override val root: Parent by fxml("/fxml/yop_select_resource.fxml")
    private val resourceComboBox1: ComboBox<String> by fxid()
    private val resourceComboBox2: ComboBox<String> by fxid()
    private val tradeButton: Button by fxid()
    init {
        resourceComboBox1.addResourceTypes()
        resourceComboBox2.addResourceTypes()
        resourceComboBox1.selectionModel.select(0)
        resourceComboBox2.selectionModel.select(0)
        tradeButton.action {
            player.resources += ResourceType.valueOf(resourceComboBox1.selectedItem!!)
            player.resources += ResourceType.valueOf(resourceComboBox2.selectedItem!!)
            block()
        }
    }
}