package dev.kason.kcatan.ui

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.paint.Color
import mu.KLogging
import tornadofx.*

object GameSettingModel {
    val playerCountProperty = SimpleStringProperty("2 Players")
    val gameIntProperty = SimpleIntegerProperty(0)
    val playerColorListProperty = SimpleStringProperty("")
}

class BoardGenerationView : View("Catan > View") {
    companion object : KLogging()

    class Styles : Stylesheet() {
        //<editor-fold desc="CSS">
        companion object {
            val boardGenerationView by cssclass()
            val boardGenerationLabel by cssclass()
            val boardGenerationButton by cssclass()
        }

        init {
            boardGenerationView {
                padding = box(10.px)
                spacing = 10.px
                alignment = Pos.CENTER
                backgroundColor += Color.BLACK
            }
            boardGenerationLabel {
                fontSize = 20.px
            }
            boardGenerationButton {
                backgroundColor += Color.DODGERBLUE
                backgroundRadius += box(0.px)
                borderRadius += box(0.px)
                borderWidth += box(0.px)
                fontSize = 20.px
                and(hover) {
                    backgroundColor += c("#1e6aff")
                }
            }
        }
        //</editor-fold>
    }

    override val root = borderpane {
        center = form {
            fieldset("Game Setting") {
                field("Player Count") {
                    label.addClass(Styles.boardGenerationLabel)
                    combobox(GameSettingModel.playerCountProperty, listOf("2 Players", "3 Players", "4 Players"))
                }
                field("Seed") {
                    label.addClass(Styles.boardGenerationLabel)
                    spinner(
                        SpinnerValueFactory.IntegerSpinnerValueFactory(Int.MIN_VALUE, Int.MAX_VALUE, 0),
                        enableScroll = true
                    ) {
                        valueProperty().integerBinding(GameSettingModel.gameIntProperty) { it ?: 0 }
                    }
                }
            }
            button("Next >") {
                action {

                }
            }.addClass(Styles.boardGenerationButton)
            addClass(Styles.boardGenerationView)
        }
        bottom = label("Group KasonG - Catan 2022 - All rights reserved")
    }
}