package dev.kason.kcatan.ui

import mu.KLogging
import tornadofx.*

class MenuView : View("Catan Menu") {
    companion object: KLogging()
    override val root = borderpane {
        bottom {
            vbox(20) {
                hbox(20) {
                    button("Create Game") {
                        action {
                            logger.info { "Test successful!" }
                        }
                        addClass(CatanStyles.menuButton)
                    }
                    button("Load Previous Game") {

                        addClass(CatanStyles.menuButton)
                    }
                    button("View Rules") {
                        action {
                            logger.info { "Size of frame: ${currentStage!!.height to currentStage!!.width}" }
                        }
                        addClass(CatanStyles.menuButton)
                    }
                    addClass(CatanStyles.centered)
                }
                label("Group KasonG - Catan 2022 - All rights reserved")
                addClass(CatanStyles.centered, CatanStyles.menuBottomSection)
            }
        }
        top {
            vbox {
                label("Welcome to Catan") {
                    addClass(CatanStyles.centered, CatanStyles.menuTitle)
                }
                addClass(CatanStyles.centered, CatanStyles.menuTopSection)

            }
        }
        minHeight = 360.0
        minWidth = 600.0
    }
}