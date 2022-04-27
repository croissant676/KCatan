/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.catan
import dev.kason.catan.core.Game
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Parent
import tornadofx.*

class GameView(private val game: Game = Game) : View(catan("Game")) {
    private val sidePanelViewProperty = SimpleObjectProperty<View>(BaseSidePanel(game.currentPlayer))
    override val root: Parent = borderpane {
        left {
            add(BoardView(game.board))
        }
        sidePanelViewProperty.addListener { _, oldValue, newValue ->
            oldValue.replaceWith(newValue, ViewTransition.Fade(0.5.seconds))
        }
        right { add(sidePanelViewProperty.value) }
    }
}