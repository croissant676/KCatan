package dev.kason.catan.core.test

import dev.kason.catan.core.Game
import dev.kason.catan.core.board.Board
import dev.kason.catan.core.board.Location
import dev.kason.catan.ui.GameCreationSettings.gameName
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
    fun possibleSettlementsTest() {
        val game = Game()

    }

}

