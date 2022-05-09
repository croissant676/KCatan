/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui.trade

import dev.kason.catan.catan
import dev.kason.catan.catanAlert
import dev.kason.catan.core.Game
import dev.kason.catan.core.player.*
import dev.kason.catan.ui.GameView
import dev.kason.catan.ui.side.BaseSidePanel
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import mu.KLogging
import tornadofx.*


class OthersTradeFragmentWrap(val player: Player, val to: Player) : Fragment() {
    companion object : KLogging()

    private val gameView: GameView by inject()
    private lateinit var getTradeData: () -> TradeInstance?
    override val root: Parent = borderpane {
        top {
            val othersTradeFragment = OthersTradeFragment(player, to)
            add(othersTradeFragment)
            getTradeData = { othersTradeFragment.getTradeResult() }
        }
        bottom {
            add(TradeResourceFragment(player.resources) {
                val tradeInstance = getTradeData() ?:
                        return@TradeResourceFragment
                TradeVerification(
                    player,
                    to, {
                        logger.debug { "Trade instance $tradeInstance declined." }
                        gameView.sidePanel = BaseSidePanel(player)
                    }, {
                        logger.debug { "Trade instance $tradeInstance accepted." }
                        player.resources += tradeInstance.result
                        player.resources -= tradeInstance.trade
                        to.resources += tradeInstance.trade
                        to.resources -= tradeInstance.result
                        gameView.sidePanel = BaseSidePanel(player)
                    }
                ).openModal()
            })
        }
    }

    @Suppress("UNCHECKED_CAST")
    class OthersTradeFragment(private val trader: Player, val to: Player) : TradeFragment() {
        companion object : KLogging()

        override val root: Parent by fxml("/fxml/trade.fxml")
        private val tradePropertySpinners: Map<ResourceType, Spinner<Int>> =
            hashMapOf<ResourceType, Spinner<Int>>().apply {
                this += (ResourceType.values().map {
                    val spinner = fxmlLoader.namespace["tradeSpinner${it.name}"] as Spinner<Int>
                    spinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, trader.resources[it]!!, 0)
                    it to spinner
                })
            }
        private val forPropertySpinners: Map<ResourceType, Spinner<Int>> =
            hashMapOf<ResourceType, Spinner<Int>>().apply {
                this += (ResourceType.values().map {
                    val spinner = fxmlLoader.namespace["forSpinner${it.name}"] as Spinner<Int>
                    spinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, to.resources[it]!!, 0)
                    it to spinner
                })
            }

        override fun getTradeResult(): TradeInstance? {
            if (tradePropertySpinners.values.all { it.value == 0 }) {
                catanAlert(
                    "Invalid trade",
                    "You have to trade something."
                )
                return null
            } else if (forPropertySpinners.values.all { it.value == 0 }) {
                catanAlert(
                    "Invalid trade",
                    "You can't trade for nothing."
                )
                return null
            }
            val instance = TradeInstance(
                tradePropertySpinners.mapValues { it.value.value },
                forPropertySpinners.mapValues { it.value.value }
            )
            if (instance.trade has instance.result) {
                catanAlert(
                    "Invalid trade",
                    "You can't trade for more than you're giving."
                )
                return null
            } else if (instance.result has instance.trade) {
                catanAlert(
                    "Invalid trade",
                    "You can't trade for less than you're giving."
                )
                return null
            }
            return instance
        }
    }

    class TradeVerification(
        player: Player,
        player1: Player,
        declineBlock: () -> Unit,
        agreeBlock: () -> Unit
    ) : Fragment(catan("Trade Verification")) {
        companion object : KLogging()

        override val root: Parent by fxml("/fxml/trade_verify.fxml")
        private val decline0: Button by fxid()
        private val decline1: Button by fxid()
        private val agree0: Button by fxid()
        private val agree1: Button by fxid()
        private val trader: Text by fxid()
        private val to: Text by fxid()

        init {
            trader.text = player.name
            to.text = player1.name
            var player0Agree = false
            var player1Agree = false
            agree0.action {
                decline0.isDisable = true
                if (player1Agree) {
                    close()
                    agreeBlock()
                } else {
                    player0Agree = true
                }
            }
            agree1.action {
                decline1.isDisable = true
                if (player0Agree) {
                    close()
                    agreeBlock()
                } else {
                    player1Agree = true
                }
            }
            decline0.action {
                close()
                declineBlock()
            }
            decline1.action {
                close()
                declineBlock()
            }
        }
    }

}

@Suppress("unused")
class OthersTradeSelection(val player: Player, val game: Game): Fragment() {
    private val othersTradeSelections = mutableListOf<OthersTradeSelectionSingle>()
    private val gameView: GameView by inject()
    override val root = borderpane {
        val players = game.players.filter { it != player }
        top {
            add(OthersTradeSelectionSingle(players.first(), game).apply {
                othersTradeSelections += this
                showButton.action {
                    othersTradeSelections.filter { it != this }.forEach { it.hideLabels() }
                    if (showing) {
                        hideLabels()
                    } else {
                        showLabels()
                    }
                }
                tradeButton.action {
                    gameView.sidePanel = OthersTradeFragmentWrap(
                        this@OthersTradeSelection.player,
                        this@apply.player
                    )
                }
            })
        }
        center {
            if (1 in players.indices) add(OthersTradeSelectionSingle(players[1], game).apply {
                othersTradeSelections += this
                showButton.action {
                    othersTradeSelections.filter { it != this }.forEach { it.hideLabels() }
                    if (showing) {
                        hideLabels()
                    } else {
                        showLabels()
                    }
                }
                tradeButton.action {
                    gameView.sidePanel = OthersTradeFragmentWrap(
                        this@OthersTradeSelection.player,
                        this@apply.player
                    )
                }
            })
        }
        bottom {
            if (2 in players.indices) add(OthersTradeSelectionSingle(players[2], game).apply {
                othersTradeSelections += this
                showButton.action {
                    othersTradeSelections.filter { it != this }.forEach { it.hideLabels() }
                    if (showing) {
                        hideLabels()
                    } else {
                        showLabels()
                    }
                }
                tradeButton.action {
                    gameView.sidePanel = OthersTradeFragmentWrap(
                        this@OthersTradeSelection.player,
                        this@apply.player
                    )
                }
            })
        }
    }

    class OthersTradeSelectionSingle(
        val player: Player,
        val game: Game,
    ) : Fragment() {
        companion object : KLogging()

        private val gameView: GameView by inject()
        override val root: Parent by fxml("/fxml/trade_others.fxml")
        private val resourceMap: Map<ResourceType, Text> = ResourceType.values().associateWith {
            (fxmlLoader.namespace["${it.name.lowercase()}Resources"] as Text)
        }
        private val totalResources: Text by fxid()
        internal val tradeButton: Button by fxid()
        internal val showButton: Button by fxid()
        private val backgroundRect: Rectangle by fxid()
        private val largestRoad: Text by fxid()
        private val largestArmy: Text by fxid()

        var showing = false

        init {
            backgroundRect.fill = player.color.jfxColor
            resourceMap.forEach { (_, text) ->
                text.text = "???"
            }
            val total = player.resources.values.sum()
            totalResources.text = total.toString()
            if (total > 7) totalResources.fill = c("#ff002d")
            tradeButton.apply {
                isDisable = true
            }
            if (game.calculateLongestRoad() == player) {
                largestRoad.isVisible = true
            }
            if (game.largestArmy() == player) {
                largestArmy.isVisible = true
            }
        }

        fun hideLabels() {
            showing = false
            resourceMap.values.forEach {
                it.text = "???"
            }
            tradeButton.isDisable = true
            showButton.text = "Show"
        }

        fun showLabels() {
            showing = true
            showButton.text = "Hide"
            tradeButton.isDisable = false
            resourceMap.forEach { (type, text) ->
                text.text = player.resources[type]?.toString() ?: "0"
            }
        }
    }
}