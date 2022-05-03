/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

@file:Suppress("DuplicatedCode")

package dev.kason.catan.ui

import dev.kason.catan.catanAlert
import dev.kason.catan.core.board.Port
import dev.kason.catan.core.player.*
import javafx.scene.Parent
import javafx.scene.control.*
import mu.KLogging
import tornadofx.*

class MaritimeTradeFragment(
    val player: Player,
    val port: Port,
    private val tradeFragment: TradeFragment
) : Fragment() {
    lateinit var getTradeData: () -> TradeInstance?
    private val gameView: GameView by inject()

    companion object : KLogging()

    override val root: Parent = borderpane {
        top {
            getTradeData = { tradeFragment.getTradeResult() }
            add(tradeFragment)
        }
        bottom {
            add(TradeResourceFragment(player.resources) {
                val tradeInstance = getTradeData()
                    ?: return@TradeResourceFragment logger.debug { "Backed off trade due to null trade resource result." }
                logger.debug { "Attempting a trade: $tradeInstance" }
                if (player.resources doesNotHave tradeInstance.trade) {
                    logger.warn { "Player ${player.name} does not have resources to trade" }
                    catanAlert(
                        "Insufficient resources",
                        "You do not have enough resources to trade."
                    )
                    return@TradeResourceFragment
                }
                player.resources -= (tradeInstance.trade)
                player.resources += (tradeInstance.result)
                gameView.sidePanel = BaseSidePanel(player)
            })
        }
    }

    class MaritimeTradeGenericFragment(val player: Player) : TradeFragment() {
        override val root: Parent by fxml("/fxml/maritime_trade_generic.fxml")
        private val tradeComboBox: ComboBox<String> by fxid()
        private val forComboBox: ComboBox<String> by fxid()
        private val repeatSpinner: Spinner<Int> by fxid()

        init {
            tradeComboBox.addResourceTypes()
            forComboBox.addResourceTypes()
            repeatSpinner.valueFactory =
                SpinnerValueFactory.IntegerSpinnerValueFactory(1, player.resources.values.sum() / 4)
        }

        override fun getTradeResult(): TradeInstance? {
            val trade = ResourceMap().resources
            val result = ResourceMap().resources
            val tradeValue = tradeComboBox.value
            val forValue = forComboBox.value
            if (tradeValue == forValue) {
                catanAlert(
                    "Invalid trade",
                    "You cannot trade for the same resource type."
                )
                return null
            }
            tradeValue?.let {
                trade[ResourceType.valueOf(it)] = repeatSpinner.value * 3
            } ?: catanAlert("Invalid trade", "You must trade least one resource.")
            forValue?.let {
                result[ResourceType.valueOf(it)] = repeatSpinner.value
            } ?: catanAlert("Invalid trade", "You must trade for least one resource.")
            return TradeInstance(trade, result)
        }
    }

    class MaritimeTradeSpecialFragment(val player: Player, val port: Port) : TradeFragment() {
        override val root: Parent by fxml("/fxml/maritime_trade_special.fxml")
        private val tradeComboBox: ComboBox<String> by fxid()
        private val repeatSpinner: Spinner<Int> by fxid()
        private val resourceLabel: Label by fxid()
        private val titleLabel: Label by fxid()

        init {
            resourceLabel.text = port.resourceType!!.name
            titleLabel.text = "Port trade for ${port.resourceType.name}"
            tradeComboBox.addResourceTypes()
        }

        override fun getTradeResult(): TradeInstance {
            val trade = ResourceMap().resources
            val result = ResourceMap().resources
            val tradeValue = tradeComboBox.value
            tradeValue?.let {
                trade[ResourceType.valueOf(it)] = repeatSpinner.value * 2
            } ?: catanAlert("Invalid trade", "You must trade least one resource.")
            result[port.resourceType!!] = repeatSpinner.value
            return TradeInstance(trade, result)
        }
    }
}

class MaritimeTradeSelectorFragment(val player: Player, accessiblePorts: List<Port>) : Fragment() {
    override val root: Parent by fxml("/fxml/maritime_trade_select.fxml")
    private val gameView: GameView by inject()
    private val portComboBox: ComboBox<String> by fxid()
    private val selectButton: Button by fxid()

    init {
        val names = accessiblePorts.map { it.resourceType }.toSet().map { it?.name ?: "Generic" }
        portComboBox.items = names.toObservable()
        selectButton.action {
            val name = portComboBox.value
            if (name.isEmpty()) {
                catanAlert("Invalid trade", "You must select a port.")
                return@action
            }
            if (name == "Generic") {
                val fragment = MaritimeTradeFragment(
                    player,
                    accessiblePorts.first(),
                    MaritimeTradeFragment.MaritimeTradeGenericFragment(player)
                )
                gameView.sidePanel = fragment
            } else {
                val port = accessiblePorts.find { it.resourceType?.name == name }
                if (port != null) {
                    val fragment = MaritimeTradeFragment(
                        player,
                        port,
                        MaritimeTradeFragment.MaritimeTradeSpecialFragment(player, port)
                    )
                    gameView.sidePanel = fragment
                } else {
                    catanAlert("Invalid trade", "You cannot trade for this port.")
                }
            }
        }
    }
}