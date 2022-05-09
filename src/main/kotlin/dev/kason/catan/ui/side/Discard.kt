/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui.side

import dev.kason.catan.catan
import dev.kason.catan.catanAlert
import dev.kason.catan.core.game
import dev.kason.catan.core.player.Player
import dev.kason.catan.core.player.ResourceType
import dev.kason.catan.ui.GameView
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.shape.Circle
import mu.KLogging
import tornadofx.*

class DiscardFragment(player: Player, private val players: ArrayDeque<Player>) : Fragment() {
    override val root: Parent = borderpane {
        top {
            add(DiscardSelector(player, players))
        }
        bottom {
            add(DiscardSidePanelView())
        }
    }

    class DiscardSidePanelView : View() {
        override val root: Parent by fxml("/fxml/discard_side_info.fxml")
    }

    class DiscardSelector(private val player: Player, private val players: ArrayDeque<Player>) : Fragment() {
        override val root: Parent by fxml("/fxml/discard.fxml")
        private val tradePropertySpinners: Map<ResourceType, Spinner<Int>> =
            hashMapOf<ResourceType, Spinner<Int>>().apply {
                this += (ResourceType.values().map {
                    @Suppress("UNCHECKED_CAST") val spinner =
                        fxmlLoader.namespace["tradeSpinner${it.name}"] as Spinner<Int>
                    spinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, player.resources[it]!!, 0)
                    it to spinner
                })
            }
        private val finishButton: Button by fxid()
        private val gameView: GameView by inject()

        init {
            finishButton.action {
                val sum = tradePropertySpinners.values.sumOf { it.value }
                if (sum >= 7) {
                    catanAlert(
                        "You need to discard more cards.",
                        "You need to have less than 7 cards in your hand after discarding."
                    )
                    return@action
                }
                ResourceType.values().forEach {
                    player.resources[it] = tradePropertySpinners[it]!!.value
                }
                val first = players.removeFirstOrNull()
                if (first == null) {
                    gameView.sidePanel = BaseSidePanel(game.currentPlayer)
                    return@action
                }
                replaceWith(SideNextPlayerView(first, players))
            }

        }
    }
}

class SideNextPlayerView(val player: Player, private val players: ArrayDeque<Player>) : View(catan("Next Player")) {
    companion object : KLogging()

    private val gameView: GameView by inject()
    override val root: Parent by fxml("/fxml/player_turn.fxml")
    private val nextPlayerLabel: Label by fxid()
    private val continueButton: Button by fxid()
    private val playerCircle: Circle by fxid()

    init {
        nextPlayerLabel.text = "Discard: ${player.color.name}"
        continueButton.action {
            gameView.sidePanel = DiscardFragment(player, players)
        }
        playerCircle.fill = player.color.jfxColor
    }

    override fun onDock() {
        primaryStage.sizeToScene()
    }
}