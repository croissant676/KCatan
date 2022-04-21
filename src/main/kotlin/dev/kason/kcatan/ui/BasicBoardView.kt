package dev.kason.kcatan.ui

import javafx.scene.layout.*
import tornadofx.*
import mu.KLogging
import javafx.scene.paint.Color

class BasicBoardView : View() {
    companion object : KLogging()

    override val root: AnchorPane by fxml("/resources/BasicBoardView.fxml") {
        button("Roll Dice") {
            action{

            }
        }
    }


    class BoardStyles : Stylesheet() { //just copying code dont mind me <33
        companion object {
            val TradeActionButton by cssclass()
            val BuildActionButton by cssclass()
            val DiceButton by cssclass() // roll and pass
            val ResourceLabel by cssclass() // idk abt this bruh

        }

        init{
            TradeActionButton{
                padding = box(2.px)
                backgroundColor += Color.YELLOW

            }

            BuildActionButton{
                padding = box(2.px)
                backgroundColor += Color.RED
            }
        }

    }


} // waht the fawc am i doing