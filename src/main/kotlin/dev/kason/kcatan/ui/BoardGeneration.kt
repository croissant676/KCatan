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
    val comboboxGenerationProperty = SimpleIntegerProperty(0)
    val gameIntProperty = SimpleIntegerProperty(0)
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
            fieldset("Board generation") {
                field("Clockwise") {
                    label.addClass(Styles.boardGenerationLabel)
                    checkbox()
                }
                field("First Tile") {
                    label.addClass(Styles.boardGenerationLabel)
                    combobox(
                        GameSettingModel.comboboxGenerationProperty,
                        listOf(0, 1, 2, 3, 6, 7, 11, 12, 15, 16, 17, 18)
                    )
                }
                hbox(20) {
                    button("Generate") {
                        action {
                            logger.info("Generate button clicked")
                        }
                    }.addClass(Styles.boardGenerationButton)
                    button("Generate all randomly") {
                        action {
                            logger.info("Generate all randomly button clicked")
                        }
                    }.addClass(Styles.boardGenerationButton)
                }
            }
            addClass(Styles.boardGenerationView)
        }
    }
}