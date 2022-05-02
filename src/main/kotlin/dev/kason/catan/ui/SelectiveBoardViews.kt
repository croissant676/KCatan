/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import javafx.scene.Parent
import tornadofx.Fragment

class RoadSelectiveView(): Fragment() {
    override val root: Parent by fxml("/fxml/board.fxml")
}