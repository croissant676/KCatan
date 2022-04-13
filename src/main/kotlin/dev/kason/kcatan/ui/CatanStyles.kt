package dev.kason.kcatan.ui

import javafx.geometry.Pos
import javafx.scene.paint.*
import javafx.scene.shape.Polygon
import mu.KLogging
import tornadofx.*

class CatanStyles : Stylesheet() {
    companion object : KLogging() {

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
        comboBox {
            fontFamily = "Century Gothic"
            fontSize = 20.px
            textFill = Color.WHITE
        }
    }
}