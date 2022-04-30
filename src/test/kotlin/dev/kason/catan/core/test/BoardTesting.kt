package dev.kason.catan.core.test

import com.google.common.io.Resources
import dev.kason.catan.core.Game
import dev.kason.catan.core.board.Board
import dev.kason.catan.core.board.Edge
import dev.kason.catan.core.board.Location
import dev.kason.catan.core.game
import dev.kason.catan.core.player.Player
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
    fun longestRoadTest() {
        game = Game(Random(2),4, listOf(Player.Color.Red,Player.Color.Red,Player.Color.Red,Player.Color.Red),"test")
        logger.debug(game.board.debugString())
        var player = Player(69, Player.Color.Red)
        player.giveRoad(game.board.edges.get(0))
        player.giveRoad(game.board.edges.get(1))
        player.giveRoad(game.board.edges.get(2))
        player.giveRoad(game.board.edges.get(3))
        player.giveRoad(game.board.edges.get(4))
        player.giveRoad(game.board.edges.get(5))
        player.giveRoad(game.board.edges.get(18))
        player.giveRoad(game.board.edges.get(9))
        player.giveRoad(game.board.edges.get(21))
        player.giveRoad(game.board.edges.get(22))
        player.giveRoad(game.board.edges.get(23))
        player.giveRoad(game.board.edges.get(36))
        player.giveRoad(game.board.edges.get(38))
        player.giveRoad(game.board.edges.get(50))
        player.giveRoad(game.board.edges.get(40))
        logger.debug(player.roads.toString())
        logger.debug(game.longestRoad(player).toString())
    }
}

