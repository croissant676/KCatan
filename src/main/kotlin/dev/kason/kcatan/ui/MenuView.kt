package dev.kason.kcatan.ui

import javafx.scene.paint.*
import mu.KLogging
import tornadofx.*

class MenuView : View("Menu") {
    companion object : KLogging()

    class Styles : Stylesheet() {
        //<editor-fold desc="CSS">
        companion object {
            val background by cssclass()
            val title by cssclass()
            val grass by cssclass()
            val menuButton by cssclass()
        }

        init {
            background {
                backgroundColor += RadialGradient(
                    90.0,
                    0.0,
                    300.0,
                    300.0,
                    350.0,
                    false,
                    CycleMethod.REFLECT,
                    listOf(
                        Stop(0.0, c("ffeccd")),
                        Stop(0.25, c("ffcf80")),
                        Stop(0.45, c("ffaf80")),
                        Stop(0.6, c("ffaea4")),
                        Stop(0.8, c("ffa4c8"))
                    )
                )
                padding = box(120.px)
            }
            menuButton {
                fontFamily = "Century Gothic"
                fontSize = 20.px
                textFill = Color.WHITE
                borderColor += box(c("b2b2b2"))
                borderWidth += box(2.px)
                padding = box(10.px)
                borderRadius += box(0.px)
                backgroundRadius += box(0.px)
                backgroundColor += c("6fc56f")
                and(hover) {
                    backgroundColor += c("4bb64b")
                }
            }
            grass {
                padding = box(40.px)
                backgroundColor += c("43a543")
                borderColor += box(c("b2b2b2"))
                borderWidth += box(2.px, 0.px, 0.px, 0.px)
            }
        }
        //</editor-fold>
        // ^ Fold open to see CSS
    }

    override val root = borderpane {
        minHeight = 360.0
        minWidth = 600.0
        bottom = vbox(20) {
            hbox(20) {
                button("Create Game", ) {
                    action {
                        replaceWith(BoardGenerationView::class, ViewTransition.Fade(0.3.seconds))
                    }
                }.addClass(Styles.menuButton)
                button("Load Previous Game").addClass(Styles.menuButton)
                button("View Rules").addClass(Styles.menuButton)
            }
            label("Group KasonG - Catan 2022 - All rights reserved")
            addClass(Styles.grass)
        }
        top = vbox {
            label("Welcome to Catan") {
                style (append = true) {
                    fontSize = 40.px
                }
            }
            addClass(Styles.background)
        }
    }

//    override fun onDock() {
//        currentStage!!.isResizable = false
//    }
}