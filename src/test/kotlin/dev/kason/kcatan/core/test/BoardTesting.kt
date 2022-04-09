package dev.kason.kcatan.core.test

import dev.kason.kcatan.core.board.Board
import org.junit.jupiter.api.Test
import kotlin.test.assertContains

class BoardTesting {
    @Test
    fun boardTest() {
        val board = Board()
        val tile = board.tiles[15]
        assertContains(tile.neighbors.values, board.tiles[14])
        assertContains(tile.neighbors.values, board.tiles[10])
        assertContains(tile.neighbors.values, board.tiles[11])
        assertContains(tile.neighbors.values, board.tiles[18])
    }
}