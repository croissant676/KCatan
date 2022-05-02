/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.core.Game
import dev.kason.catan.core.player.Player
import dev.kason.catan.core.player.ResourceType
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text
import mu.KLogging
import tornadofx.*


class OthersTradeFragmentWrap(val player: Player) : Fragment() {
    companion object : KLogging()

    private lateinit var getTradeData: () -> TradeInstance
    override val root: Parent = borderpane {
        top {
            val othersTradeFragment = OthersTradeFragment(player, player)
            add(othersTradeFragment)
            getTradeData = { othersTradeFragment.getTradeResult() }
        }
        bottom {
            add(TradeResourceFragment(player.resources) {
                logger.info { "Testing trade resource fragment" }
            })
        }
    }

    @Suppress("UNCHECKED_CAST")
    class OthersTradeFragment(val trader: Player, val to: Player) : Fragment() {
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

        fun getTradeResult(): TradeInstance = TradeInstance(
            tradePropertySpinners.mapValues { it.value.value },
            forPropertySpinners.mapValues { it.value.value }
        )
    }

}

@Suppress("unused")
class OthersTradeSelection(val player: Player, val game: Game): Fragment() {
    override val root: Parent by fxml("/fxml/trade_others.fxml")
    private val pane0: AnchorPane by fxid()
    private val resourceMap0: Map<ResourceType, Text> = ResourceType.values().associateWith {
        (fxmlLoader.namespace["${it.name.lowercase()}Resources0"] as Text)
    }
    private val totalResources0: Text by fxid()
    private val tradeButton0: Button by fxid()
    private val showButton0: Button by fxid()
    private val pane1: AnchorPane by fxid()
    private val resourceMap1: Map<ResourceType, Text> = ResourceType.values().associateWith {
        (fxmlLoader.namespace["${it.name.lowercase()}Resources1"] as Text)
    }
    private val totalResources1: Text by fxid()
    private val tradeButton1: Button by fxid()
    private val showButton1: Button by fxid()
    private val pane2: AnchorPane by fxid()
    private val resourceMap2: Map<ResourceType, Text> = ResourceType.values().associateWith {
        (fxmlLoader.namespace["${it.name.lowercase()}Resources2"] as Text)
    }
    private val totalResources2: Text by fxid()
    private val tradeButton2: Button by fxid()
    private val showButton2: Button by fxid()

    private val panes = listOf(pane0, pane1, pane2)
    private val resourceMaps = listOf(resourceMap0, resourceMap1, resourceMap2)
    private val totalResources = listOf(totalResources0, totalResources1, totalResources2)
    private val tradeButtons = listOf(tradeButton0, tradeButton1, tradeButton2)
    private val showButtons = listOf(showButton0, showButton1, showButton2)
    init {
        val list = game.players.filter { it != player }

    }
}