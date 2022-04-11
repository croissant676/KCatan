package dev.kason.kcatan.ui

import javafx.geometry.Pos
import javafx.scene.paint.*
import mu.KLogging
import tornadofx.*

class CatanStyles : Stylesheet() {
    companion object : KLogging() {
        val menuTitle by cssclass()
        val playerSelectBackground by cssclass()
        val genericDarkButton by cssclass()
    }

    init {
        container {
            alignment = Pos.CENTER
        }
        button {
            fontFamily = "Century Gothic"
            fontSize = 20.px
            textFill = Color.WHITE
            borderColor += box(c("b2b2b2"))
            borderWidth += box(2.px)
        }
        label {
            fontFamily = "Century Gothic"
            fontSize = 20.px
            textFill = Color.WHITE
        }
        menuTitle {
            fontSize = 40.px
            fontFamily = "Century Gothic"
            textFill = Color.WHITE
        }
        playerSelectBackground {
            backgroundColor += c("000000")
            padding = box(10.px)
        }
        comboBox {
            fontFamily = "Century Gothic"
            fontSize = 20.px
            textFill = Color.WHITE
        }
        genericDarkButton {
            backgroundColor += c("1e90ff")
            padding = box(10.px)
            borderColor += box(c("b2b2b2"))
            borderWidth += box(2.px)
            and(hover) {
                backgroundColor += c("528fcb")
            }
        }
    }
}