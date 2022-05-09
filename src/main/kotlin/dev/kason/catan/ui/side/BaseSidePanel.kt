/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui.side

import dev.kason.catan.catanAlert
import dev.kason.catan.core.Constants
import dev.kason.catan.core.game
import dev.kason.catan.core.player.*
import dev.kason.catan.ui.GameView
import dev.kason.catan.ui.board.CitySelectionFragment
import dev.kason.catan.ui.trade.*
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.text.Text
import mu.KLogging
import tornadofx.*

class BaseSidePanel(val player: Player) : Fragment() {
    private val bottomPanelProperty = SimpleObjectProperty<UIComponent>(BaseSidePanelBottom(player))
    var bottomPanel: UIComponent by bottomPanelProperty
    override val root: BorderPane = borderpane {
        top { add(CostsFragment(player)) }
        center { add(PlayerResourceCosts(player.resources)) }
        bottomPanelProperty.addListener { _, oldValue, newValue ->
            oldValue.replaceWith(newValue, ViewTransition.Fade(0.5.seconds))
        }
        bottom {
            add(bottomPanel)
        }
    }
}

class BaseSidePanelBottom(val player: Player) : View() {
    companion object : KLogging()

    private val gameView: GameView by inject()
    override val root: Parent by fxml("/fxml/base_side.fxml")
    private val buildCity: Button by fxid()
    private val buildConstruction: Button by fxid()
    private val devCardBuyButton: Button by fxid()
    private val maritimeTradeButton: Button by fxid()
    private val devCardUseButton: Button by fxid()
    private val defaultTradeButton: Button by fxid()
    private val othersTradeButton: Button by fxid()

    fun updateButtons() {
        val turn = game.currentTurn
        devCardUseButton.isDisable = turn.usedDevelopmentCard
        buildCity.isDisable = !turn.rolledDice
        buildConstruction.isDisable = !turn.rolledDice
        devCardBuyButton.isDisable = !turn.rolledDice
        maritimeTradeButton.isDisable = !turn.rolledDice
        othersTradeButton.isDisable = !turn.rolledDice
        defaultTradeButton.isDisable = !turn.rolledDice
    }

    init {
        buildCity.action {
            logger.debug { "Attempted to build a city: $player :: ${player.resources}" }
            if (player.resources doesNotHave Constants.cityCost) {
                catanAlert(
                    "Not enough resources",
                    "You do not have enough resources to build a city."
                )
                return@action
            }
            val citySelectionFragment = CitySelectionFragment(player, game.board)
            gameView.boardPanel = citySelectionFragment
            val cityConstructionFragment = CityConstructionPanel(citySelectionFragment, player)
            gameView.sidePanel = cityConstructionFragment
        }
        devCardBuyButton.action {
            logger.debug { "Attempted to buy a dev card: $player :: ${player.resources}" }
            if (player.resources doesNotHave Constants.developmentCardCost) {
                catanAlert(
                    "Not enough resources",
                    "You do not have enough resources to buy a development card."
                )
            } else {
                game.buyDevelopmentCard(player)
            }
        }
        devCardUseButton.action {
            if (player.developmentCards.all { it.value == 0 }) {
                catanAlert(
                    "No development cards",
                    "You do not have any development cards to use."
                )
            } else {
                gameView.sidePanel = DevCardsSidePanel(player)
            }
        }
        buildConstruction.action {
            logger.info { "Switching to the build construction from base side panel." }
            (gameView.sidePanel as? BaseSidePanel)?.bottomPanel = ConstSelectorView(player)
        }
        defaultTradeButton.action {
            gameView.sidePanel = DefaultTradeFragmentWrap(player)
        }
        othersTradeButton.action {
            gameView.sidePanel = OthersTradeSelection(player, game)
        }
        maritimeTradeButton.action {
            val possiblePorts = player.accessiblePorts()
            if(possiblePorts.isEmpty()) {
                catanAlert(
                    "No ports",
                    "You do not have any ports to trade with."
                )
                return@action
            }
            gameView.sidePanel = MaritimeTradeSelectorFragment(player, possiblePorts)
        }
        updateButtons()
    }
}

class CostsFragment(private val player: Player) : Fragment() {
    override val root: AnchorPane by fxml("/fxml/costs.fxml")
    private val roadLine: Line by fxid()
    private val settlementCircle: Circle by fxid()
    private val cityCircle: Circle by fxid()
    private val rootPane: AnchorPane by fxid()

    private val roadLeft: Text by fxid()
    private val cityLeft: Text by fxid()
    private val settlementLeft: Text by fxid()
    private val devCardLeft: Text by fxid()

    init {
        val color = player.color.jfxColor
        roadLine.stroke = color
        settlementCircle.fill = color
        cityCircle.fill = color
        rootPane.style = "-fx-background-color: ${color.css};"
        update()
    }

    private fun update() {
        devCardLeft.text = "${game.developmentCardDeck.size} left"
        roadLeft.text = "${player.roadsLeft} left"
        settlementLeft.text = "${player.settlementsLeft} left"
        cityLeft.text = "${player.citiesLeft} left"
    }
}

class PlayerResourceCosts(playerResources: PlayerResourceMap): Fragment() {
    override val root: Parent by fxml("/fxml/player_resources.fxml")
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
        val total = playerResources.resources.values.sum()
        totalResources.text = total.toString()
        if(total > 7) {
            totalResources.fill = c("#ff002d")
        }
    }
}