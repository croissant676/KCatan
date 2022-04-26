package dev.kason.catan.core.test

import dev.kason.catan.core.board.Board
import dev.kason.catan.core.board.Location
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
        assertEquals(board.tiles[6].neighbors[Location.Left], board.tiles[5])
        assertEquals(board.tiles[6].neighbors[Location.BottomRight], board.tiles[11])
        assertEquals(board.tiles[7].neighbors[Location.BottomRight], board.tiles[12])
        assertEquals(board.tiles[8].neighbors[Location.TopRight], board.tiles[4])
        assertEquals(board.tiles[9].neighbors[Location.TopLeft], board.tiles[4])
        assertEquals(board.tiles[10].neighbors[Location.Right], board.tiles[11])
        assertEquals(board.tiles[10].neighbors[Location.BottomRight], board.tiles[15])
        assertEquals(board.tiles[12].neighbors[Location.TopRight], board.tiles[8])
        assertEquals(board.tiles[14].neighbors[Location.Left], board.tiles[13])
    }

    @Test
    fun assertTileAndVerticesMatch() {
        val board = Board(Random)
        board.tiles.forEach { tile ->
            assertTrue(tile.vertices.all { (location, vertex) -> vertex.tiles[location.opposite] == tile })
        }
    }

}

