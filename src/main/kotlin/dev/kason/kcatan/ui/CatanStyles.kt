package dev.kason.kcatan.ui

import javafx.geometry.Pos
import javafx.scene.paint.*
import mu.KLogging
import tornadofx.*

class CatanStyles : Stylesheet() {
    companion object : KLogging() {
        val menuTitle by cssclass()
        val menuButton by cssclass()
        val centered by cssclass()
        val menuBottomSection by cssclass()
        val menuTopSection by cssclass()
    }

    init {
        centered {
            alignment = Pos.CENTER
        }
        button {
            fontFamily = "Century Gothic"
            fontSize = 20.px
            textFill = Color.WHITE
            borderColor += box(c("b2b2b2"))
            borderWidth += box(2.px)
        }
        menuButton {
            padding = box(10.px)
            borderRadius += box(0.px)
            backgroundRadius += box(0.px)
            backgroundColor += c("55c655")
            and(hover) {
                backgroundColor += c("38a838")
            }
        }
        label {
            fontFamily = "Century Gothic"
            fontSize = 20.px
            textFill = Color.WHITE
        }
        menuBottomSection {
            padding = box(40.px)
            backgroundColor += c("3aae3a")
        }
        menuTitle {
            fontSize = 40.px
            fontFamily = "Century Gothic"
            textFill = Color.WHITE
        }
        menuTopSection {
            backgroundColor += RadialGradient(
                90.0,
                0.0,
                300.0,
                300.0,
                350.0,
                false,
                CycleMethod.REFLECT,
                mutableListOf(
                    Stop(0.0, c("ffeccd")),
                    Stop(0.25, c("ffcf80")),
                    Stop(0.45, c("ffaf80")),
                    Stop(0.6, c("ffaea4")),
                    Stop(0.8, c("ffa4c8"))
                )
            )
            padding = box(120.px)
        }
    }
}