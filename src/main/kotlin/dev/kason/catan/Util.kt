/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan

import com.google.common.io.Resources
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.Window
import tornadofx.addClass

fun catan(name: String) = "Catan > $name"

@Suppress("UnstableApiUsage")
inline fun catanAlert(
    header: String,
    content: String,
    owner: Window? = null,
    title: String = catan("Alert"),
    type: Alert.AlertType = Alert.AlertType.ERROR,
    actionFn: Alert.(ButtonType) -> Unit = {}
): Alert {

    val alert = Alert(type, content)
    alert.dialogPane.stylesheets += (Resources.getResource("css/board.css").toExternalForm());
    alert.dialogPane.addClass("alert-dialog")
    alert.title = title
    alert.headerText = header
    owner?.also { alert.initOwner(it) }
    val buttonClicked = alert.showAndWait()
    if (buttonClicked.isPresent) {
        alert.actionFn(buttonClicked.get())
    }
    return alert
}