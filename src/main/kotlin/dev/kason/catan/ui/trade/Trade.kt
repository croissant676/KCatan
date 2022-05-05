/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

@file:Suppress("DuplicatedCode")

package dev.kason.catan.ui.trade

import dev.kason.catan.catanAlert
import dev.kason.catan.core.Constants
import dev.kason.catan.core.player.*
import dev.kason.catan.ui.GameView
import dev.kason.catan.ui.side.BaseSidePanel
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.text.Text
import mu.KLogging
import tornadofx.*

class TradeInstance(
    val trade: Map<ResourceType, Int> = mutableMapOf(),
    val result: Map<ResourceType, Int> = mutableMapOf(),
)

fun ComboBox<String>.addResourceTypes() {
    items.addAll(ResourceType.values().map { it.name })
}

abstract class TradeFragment: Fragment() {
    abstract fun getTradeResult(): TradeInstance?
}

class DefaultTradeFragmentWrap(val player: Player) : Fragment() {
    companion object : KLogging()

    private val gameView: GameView by inject()
    private lateinit var getTradeData: () -> TradeInstance?
    override val root: Parent = borderpane {
        top {
            val defaultTradeFragment = DefaultTradeFragment()
            getTradeData = { defaultTradeFragment.getTradeResult() }
            add(defaultTradeFragment)
        }
        bottom {
            add(TradeResourceFragment(player.resources) {
                logger.debug { "Attempting a trade." }
                val tradeInstance = getTradeData()
                    ?: return@TradeResourceFragment logger.debug { "Backed off trade due to null trade resource result." }
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

    class DefaultTradeFragment : TradeFragment() {
        override val root: Parent by fxml("/fxml/default_trade.fxml")
        private val tradeComboBox: ComboBox<String> by fxid()
        private val forComboBox: ComboBox<String> by fxid()
        private val repeatSpinner: Spinner<Int> by fxid()

        init {
            tradeComboBox.addResourceTypes()
            forComboBox.addResourceTypes()
            repeatSpinner.valueFactory =
                SpinnerValueFactory.IntegerSpinnerValueFactory(1, Constants.maxRepeat)
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
            } ?:  run {
                catanAlert("Invalid trade", "You must trade least one resource.")
                return null
            }
            forValue?.let {
                result[ResourceType.valueOf(it)] = repeatSpinner.value
            } ?: run {
                catanAlert("Invalid trade", "You must trade for least one resource.")
                return null
            }
            return TradeInstance(trade, result)
        }
    }
}

class TradeResourceFragment(playerResources: Map<ResourceType, Int>, onAction: () -> Unit): Fragment() {
    override val root: Parent by fxml("/fxml/trade_resources.fxml")
    private val tradeButton: Button by fxid()
    private val hillResources: Text by fxid()
    private val forestResources: Text by fxid()
    private val pastureResources: Text by fxid()
    private val mountainResources: Text by fxid()
    private val fieldResources: Text by fxid()
    private val totalResources: Text by fxid()
    init {
        hillResources.text = playerResources.brick.toString()
        forestResources.text = playerResources.lumber.toString()
        pastureResources.text = playerResources.wool.toString()
        mountainResources.text = playerResources.ore.toString()
        fieldResources.text = playerResources.grain.toString()
        val total = playerResources.values.sum()
        totalResources.text = total.toString()
        if (total > 7) {
            totalResources.fill = c("#ff002d")
        }
        tradeButton.action(onAction)
    }
}
