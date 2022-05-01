/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.core.player.*
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.text.Text
import mu.KLogging
import tornadofx.*

class TradeInstance(
    val trade: Map<ResourceType, Int> = mutableMapOf(),
    val result: Map<ResourceType, Int> = mutableMapOf(),
)

class DefaultTradeFragment(val player: Player) : Fragment() {
    companion object: KLogging()
    private lateinit var getTradeData: () -> TradeInstance
    override val root: Parent = borderpane {
        top {
            val tradeFragment = TradeFragment(player.resources)
            add(tradeFragment)
            getTradeData = { tradeFragment.getTradeResult() }
        }
        center {
            add(TradeResourceFragment(player.resources) {
                logger.info { "Testing trade resource fragment" }
            })
        }
    }

}

@Suppress("UNCHECKED_CAST")
class TradeFragment(private val max: Map<ResourceType, Int>) : Fragment() {
    companion object : KLogging()

    override val root: Parent by fxml("/fxml/trade.fxml")
    private val tradePropertySpinners: Map<ResourceType, Spinner<Int>> = hashMapOf<ResourceType, Spinner<Int>>().apply {
        this += (ResourceType.values().map {
            val spinner = fxmlLoader.namespace["tradeSpinner${it.name}"] as Spinner<Int>
            spinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, max[it]!!, 0)
            it to spinner
        })
    }
    private val forPropertySpinners: Map<ResourceType, Spinner<Int>> = hashMapOf<ResourceType, Spinner<Int>>().apply {
        this += (ResourceType.values().map {
            val spinner = fxmlLoader.namespace["forSpinner${it.name}"] as Spinner<Int>
            spinner.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, max[it]!!, 0)
            it to spinner
        })
    }

    fun getTradeResult(): TradeInstance = TradeInstance(
        tradePropertySpinners.mapValues { it.value.value },
        forPropertySpinners.mapValues { it.value.value }
    )
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
        if(total > 7) {
            totalResources.fill = c("#ff002d")
        }
        tradeButton.action(onAction)
    }
}