package dev.kason.catan.core.test

import dev.kason.catan.core.board.Board
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertContains

class BoardTesting {
    @Test
    fun boardTest() {
        val board = Board(Random(100))
        val tile = board.tiles[15]
        assertContains(tile.neighbors.values, board.tiles[14])
        assertContains(tile.neighbors.values, board.tiles[10])
        assertContains(tile.neighbors.values, board.tiles[11])
        assertContains(tile.neighbors.values, board.tiles[18])
    }
}