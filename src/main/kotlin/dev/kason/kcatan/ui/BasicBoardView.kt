package dev.kason.kcatan.ui

import tornadofx.*
import javafx.scene.layout.BorderPane
import mu.KLogging

class BasicBoardView : View() {
    companion object : KLogging()

    override val root: BorderPane by fxml("/resources/BasicBoardView.fxml")


} // waht the fawc am i doing