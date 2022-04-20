package dev.kason.kcatan.ui

import javafx.scene.layout.*
import tornadofx.*
import mu.KLogging

class BasicBoardView : View() {
    companion object : KLogging()

    override val root: AnchorPane by fxml("/resources/BasicBoardView.fxml") {
        button("Roll Dice") {

        }
    }


    class BoardStyles : Stylesheet() { //just copying code dont mind me <33
        companion object {

        }
    }


} // waht the fawc am i doing