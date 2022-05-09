/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.ui.side

import dev.kason.catan.catanAlert
import dev.kason.catan.core.game
import dev.kason.catan.core.player.DevCardType
import dev.kason.catan.core.player.Player
import dev.kason.catan.ui.GameView
import dev.kason.catan.ui.board.*
import dev.kason.catan.ui.dev.MonopolySelectFragment
import dev.kason.catan.ui.dev.YOPSelectFragment
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.text.Text
import tornadofx.View
import tornadofx.action

class DevCardsSidePanel(val player: Player) : View() {
    private val gameView: GameView by inject()
    override val root: Parent by fxml("/fxml/dev_card.fxml")
    private val useVPButton: Button by fxid() //victory point
    private val useKnightButton: Button by fxid()
    private val useYOPButton: Button by fxid() //year of plenty
    private val useRBButton: Button by fxid() //road building
    private val useMonopolyButton: Button by fxid()
    private val labelVP: Text by fxid()
    private val labelKnight: Text by fxid()
    private val labelYOP: Text by fxid()
    private val labelRB: Text by fxid()
    private val labelMonopoly: Text by fxid()

    init {
        labelVP.text = "Victory Point - ${player.developmentCards[DevCardType.VictoryPoint]}"
        labelKnight.text = "Knight - ${player.developmentCards[DevCardType.Knight]}"
        labelYOP.text = "Year of Plenty - ${player.developmentCards[DevCardType.YearOfPlenty]}"
        labelRB.text = "Road Building - ${player.developmentCards[DevCardType.RoadBuilding]}"
        labelMonopoly.text = "Monopoly - ${player.developmentCards[DevCardType.Monopoly]}"
        useVPButton.action {
            catanAlert(
                "Cannot use Victory Point Card",
                "You can't use Victory Point Cards, they are automatically processed."
            )
        }
        useKnightButton.action {
            if (player.developmentCards[DevCardType.Knight]!! > 0) {
                player.developmentCards[DevCardType.Knight] =
                    player.developmentCards[DevCardType.Knight]!! - 1
                player.armyStrength++
                val robberSelectionFragment = RobberSelectionFragment(game.board)
                gameView.boardPanel = robberSelectionFragment
                gameView.sidePanel = RobberSideTileSelection(robberSelectionFragment, player)
            } else {
                catanAlert(
                    "No Knight Cards",
                    "You don't have any Knight Cards."
                )
            }
        }
        useYOPButton.action {
            if (player.developmentCards[DevCardType.YearOfPlenty]!! > 0) {
                player.developmentCards[DevCardType.YearOfPlenty] =
                    player.developmentCards[DevCardType.YearOfPlenty]!! - 1
                YOPSelectFragment(player) {
                    close()
                    gameView.sidePanel = BaseSidePanel(player)
                    gameView.boardPanel = BoardView(game.board)
                }.openModal()
            } else {
                catanAlert(
                    "No Year of Plenty Cards",
                    "You don't have any Year of Plenty Cards."
                )
            }
        }
        useRBButton.action {
            if (player.developmentCards[DevCardType.RoadBuilding]!! > 0) {
                player.developmentCards[DevCardType.RoadBuilding] =
                    player.developmentCards[DevCardType.RoadBuilding]!! - 1
                val roadSelectionFragment = RoadSelectionFragment(player, game.board, game.getPossibleRoads(player))
                gameView.boardPanel = roadSelectionFragment
                gameView.sidePanel = DevCardsBuildRoadPanel(roadSelectionFragment, player)
            } else {
                catanAlert(
                    "No Road Building Cards",
                    "You don't have any Road Building Cards."
                )
            }
        }
        useMonopolyButton.action {
            if (player.developmentCards[DevCardType.Monopoly]!! > 0) {
                player.developmentCards[DevCardType.Monopoly] =
                    player.developmentCards[DevCardType.Monopoly]!! - 1
                MonopolySelectFragment(player).openModal()
            } else {
                catanAlert(
                    "No Monopoly Cards",
                    "You don't have any Monopoly Cards."
                )
            }
        }
        if (player.developmentCards[DevCardType.Knight]!! == 1 &&
            DevCardType.Knight  in game.currentTurn.drawnDevCards
        ) {
            useKnightButton.isDisable = true
        } else if (player.developmentCards[DevCardType.Monopoly]!! == 1 &&
            DevCardType.Monopoly  in game.currentTurn.drawnDevCards
        ) {
            useMonopolyButton.isDisable = true
        } else if (player.developmentCards[DevCardType.RoadBuilding]!! == 1 &&
            DevCardType.RoadBuilding  in game.currentTurn.drawnDevCards
        ) {
            useRBButton.isDisable = true
        } else if (player.developmentCards[DevCardType.YearOfPlenty]!! == 1 &&
            DevCardType.YearOfPlenty  in game.currentTurn.drawnDevCards
        ) {
            useYOPButton.isDisable = true
        }
    }
}