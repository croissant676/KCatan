package dev.kason.catan.core.test

import dev.kason.catan.core.Game
import dev.kason.catan.core.board.Board
import dev.kason.catan.core.board.Location
import dev.kason.catan.core.game
import dev.kason.catan.core.player.Player
import dev.kason.catan.core.player.ResourceType
import mu.KLogging
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue
class BoardTesting {
    companion object : KLogging()

    @Test
    fun basicTileTest() {
        val board = Board(Random(123))
        assertEquals(board[6].neighbors[Location.Left], board[5])
        assertEquals(board[6].neighbors[Location.BottomRight], board[11])
        assertEquals(board[7].neighbors[Location.BottomRight], board[12])
        assertEquals(board[8].neighbors[Location.TopRight], board[4])
        assertEquals(board[9].neighbors[Location.TopLeft], board[4])
        assertEquals(board[10].neighbors[Location.Right], board[11])
        assertEquals(board[10].neighbors[Location.BottomRight], board[15])
        assertEquals(board[12].neighbors[Location.TopRight], board[8])
        assertEquals(board[14].neighbors[Location.Left], board[13])
    }

    @Test
    fun assertTileAndVerticesMatch() {
        val board = Board(Random)
        board.forEach { tile ->
            assertTrue(tile.vertices.all { (location, vertex) -> vertex.tiles[location.opposite] == tile })
        }
    }

    @Test
    fun longestRoadTest1() {
        game = Game(
            Random(2),
            4,
            listOf(Player.Color.Red, Player.Color.Red, Player.Color.Red, Player.Color.Red),
            gameName = "test"
        )
        logger.debug(game.board.debugString())
        val player = Player(69, Player.Color.Red)
        player.giveRoad(game.board.edges[0])
        player.giveRoad(game.board.edges[1])
        player.giveRoad(game.board.edges[2])
        player.giveRoad(game.board.edges[3])
        player.giveRoad(game.board.edges[4])
        player.giveRoad(game.board.edges[5])
        player.giveRoad(game.board.edges[18])
        player.giveRoad(game.board.edges[9])
        player.giveRoad(game.board.edges[21])
        player.giveRoad(game.board.edges[22])
        player.giveRoad(game.board.edges[23])
        player.giveRoad(game.board.edges[36])
        player.giveRoad(game.board.edges[38])
        player.giveRoad(game.board.edges[50])
        player.giveRoad(game.board.edges[40])
        logger.debug(player.roads.toString())
        logger.debug(game.longestRoad(player).toString())
    }

    @Test
    fun longestRoadTest2() {
        game = Game(
            Random(2),
            4,
            listOf(Player.Color.Red, Player.Color.Red, Player.Color.Red, Player.Color.Red),
            gameName = "test"
        )
        logger.debug(game.board.debugString())
        val player = Player(69, Player.Color.Red)
        player.resources += mapOf(
            ResourceType.Brick to Int.MAX_VALUE,
            ResourceType.Grain to Int.MAX_VALUE,
            ResourceType.Lumber to Int.MAX_VALUE,
            ResourceType.Ore to Int.MAX_VALUE,
            ResourceType.Wool to Int.MAX_VALUE
        )
        game.buildRoad(player, game.board.edges[0])
        game.buildRoad(player, game.board.edges[1])
        game.buildRoad(player, game.board.edges[2])
        game.buildRoad(player, game.board.edges[3])
        game.buildRoad(player, game.board.edges[4])
        game.buildRoad(player, game.board.edges[5])
        game.buildRoad(player, game.board.edges[18])
        game.buildRoad(player, game.board.edges[9])
        game.buildRoad(player, game.board.edges[21])
        game.buildRoad(player, game.board.edges[22])
        game.buildRoad(player, game.board.edges[23])
        game.buildRoad(player, game.board.edges[36])
        game.buildRoad(player, game.board.edges[25])
        game.buildRoad(player, game.board.edges[39])
        game.buildRoad(player, game.board.edges[41])
        game.buildRoad(player, game.board.edges[40])
        logger.debug(player.roads.toString())
        logger.debug(game.longestRoad(player).toString())
    }

    @Test
    fun vertexRelationsTest() {
        val board = Board(Random)
        logger.info { board.vertices.random().edges }
        logger.info { board.edges.random().vertices }
    }
}
