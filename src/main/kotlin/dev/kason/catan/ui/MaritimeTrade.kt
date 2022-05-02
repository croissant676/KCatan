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
import tornadofx.*


class MaritimeTradeFragment(val player: Player, val port: Port) : Fragment() {
    lateinit var getTradeData: () -> TradeInstance?
    private val gameView: GameView by inject()
    override val root: Parent =  borderpane {
        top {
            val defaultTradeFragment = DefaultTradeFragmentWrap.DefaultTradeFragment(player)
            getTradeData = { defaultTradeFragment.getTradeResult() }
            add(defaultTradeFragment)
        }
        bottom {
            add(TradeResourceFragment(player.resources) {
                DefaultTradeFragmentWrap.logger.debug { "Attempting a trade." }
                val tradeInstance = getTradeData()
                    ?: return@TradeResourceFragment DefaultTradeFragmentWrap.logger.debug { "Backed off trade due to null trade resource result." }
                if (player.resources doesNotHave tradeInstance.trade) {
                    DefaultTradeFragmentWrap.logger.warn { "Player ${player.name} does not have resources to trade" }
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
                trade[ResourceType.valueOf(it)] = repeatSpinner.value * 4
            } ?: catanAlert("Invalid trade", "You must trade least one resource.")
            forValue?.let {
                result[ResourceType.valueOf(it)] = repeatSpinner.value
            } ?: catanAlert("Invalid trade", "You must trade for least one resource.")
            return TradeInstance(trade, result)
        }
    }

    class MaritimeTradeSpecialFragment(val player: Player, val port: Port) : Fragment() {
        override val root: Parent by fxml("/fxml/maritime_trade_special.fxml")
        private val tradeComboBox: ComboBox<String> by fxid()
        private val repeatSpinner: Spinner<Int> by fxid()
        private val resourceLabel: Label by fxid()
        private val titleLabel: Label by fxid()

        init {

        }
    }
}

class MaritimeTradeSelectorFragment(val player: Player) : Fragment() {
    override val root: Parent by fxml("/fxml/maritime_trade_select.fxml")
}