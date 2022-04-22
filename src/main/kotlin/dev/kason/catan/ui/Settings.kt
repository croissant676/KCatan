/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.catan
import javafx.scene.Parent
import javafx.scene.control.ComboBox
import tornadofx.View

class UserCountSettingView: View(catan("Creation")) {
    override val root: Parent by fxml("/fxml/user_count_setting.fxml")
    private val playerComboBox: ComboBox<String> by fxid()
    override fun onDock() {

    }
}