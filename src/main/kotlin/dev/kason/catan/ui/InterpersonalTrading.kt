/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui

import dev.kason.catan.core.player.Player
import dev.kason.catan.core.player.ResourceType
import javafx.scene.Parent
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
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