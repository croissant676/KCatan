/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core.board

import dev.kason.catan.core.Constants
import dev.kason.catan.core.player.ResourceType
import java.util.Collections
import mu.KLogging
import kotlin.random.Random

data class Board(
    private val random: Random,
    private val tiles: List<Tile> = generateTiles(random)
) : List<Tile> by tiles {

    companion object : KLogging()

    private val _edges = mutableListOf<Edge>()
    val edges by lazy { _edges.toList() }

    private val _vertices = mutableListOf<Vertex>()
    val vertices by lazy { _vertices.toList() }

    var robberIndex = tiles.indexOfFirst { it.type == Tile.Type.Desert }
    val robberTile get() = tiles[robberIndex]

    private val _ports = mutableListOf<Port>()
    val ports: List<Port> = _ports

    // ----------------- Initialization -------------------

    init {
        tileGraph()
        edgeGraph()
        vertexGraph()
        generateTileValues()
        generatePortValues()
    }

    private fun tileGraph() = tiles.forEach {
        if (it.id !in Constants.leftMost) it._neighbors[Location.Left] = tiles[it.id - 1]
        if (it.id !in Constants.rightMost) it._neighbors[Location.Right] = tiles[it.id + 1]
        when (it.id) {
            in 0..2 -> {
                it._neighbors[Location.BottomLeft] = tiles[it.id + 3]
                it._neighbors[Location.BottomRight] = tiles[it.id + 4]
            }
            in 3..6 -> {
                if (it.id != 3) it._neighbors[Location.TopLeft] = tiles[it.id - 4]
                if (it.id != 6) it._neighbors[Location.TopRight] = tiles[it.id - 3]
                it._neighbors[Location.BottomLeft] = tiles[it.id + 4]
                it._neighbors[Location.BottomRight] = tiles[it.id + 5]
            }
            in 7..11 -> {
                if (it.id != 7) {
                    it._neighbors[Location.TopLeft] = tiles[it.id - 5]
                    it._neighbors[Location.BottomLeft] = tiles[it.id + 4]
                }
                if (it.id != 11) {
                    it._neighbors[Location.TopRight] = tiles[it.id - 4]
                    it._neighbors[Location.BottomRight] = tiles[it.id + 5]
                }
            }
            in 12..15 -> {
                if (it.id != 12) it._neighbors[Location.BottomLeft] = tiles[it.id + 3]
                if (it.id != 15) it._neighbors[Location.BottomRight] = tiles[it.id + 4]
                it._neighbors[Location.TopLeft] = tiles[it.id - 5]
                it._neighbors[Location.TopRight] = tiles[it.id - 4]
            }
            else -> {
                it._neighbors[Location.TopLeft] = tiles[it.id - 4]
                it._neighbors[Location.TopRight] = tiles[it.id - 3]
            }
        }
    }

    private fun edgeGraph() = tiles.forEach {
        val keys = it.neighbors.keys
        for (location in Location.checkLocations) {
            if (location !in keys) continue
            val edge = it.neighbors[location]!!.edges[location.opposite]!!
            it._edges[location] = edge
            edge.second = it
        }
        val edgeKeys = it.edges.keys
        for (location in Location.edgeLocations) {
            if (location !in edgeKeys) it._edges[location] = Edge(it).apply {
                _edges += this
            }
        }
    }

    private fun vertexGraph() = tiles.forEach {
        val keys = it.neighbors.keys
        if (Location.TopLeft in keys) {
            val topLeft = it.neighbors[Location.TopLeft]!!
            it._vertices[Location.TopLeft] = topLeft.vertices[Location.Bottom]!!
            it._vertices[Location.Top] = topLeft.vertices[Location.BottomRight]!!
            if (Location.TopRight in keys) {
                val topRight = it.neighbors[Location.TopRight]!!
                it._vertices[Location.TopRight] = topRight.vertices[Location.Bottom]!!
            }
            if (Location.Left in keys) {
                val left = it.neighbors[Location.Left]!!
                it._vertices[Location.BottomLeft] = left.vertices[Location.BottomRight]!!
            }
        } else if (Location.Left in keys) {
            val left = it.neighbors[Location.Left]!!
            it._vertices[Location.BottomLeft] = left.vertices[Location.BottomRight]!!
            it._vertices[Location.TopLeft] = left.vertices[Location.TopRight]!!
        } else if (Location.TopRight in keys) {
            val topRight = it.neighbors[Location.TopRight]!!
            it._vertices[Location.TopRight] = topRight.vertices[Location.Bottom]!!
            it._vertices[Location.Top] = topRight.vertices[Location.BottomLeft]!!
        }
        for (location in Location.vertexLocations) {
            if (location !in it.vertices.keys) it._vertices[location] = Vertex()
            val vertex = it._vertices[location] ?: run {
                val newVertex = Vertex().apply {
                    it._vertices[location] = this
                }
                it._vertices[location] = newVertex
                newVertex
            }
            vertex._tiles[location.opposite] = it
        }
        _vertices += it._vertices.values
    }

    private fun generateTileValues(
        firstTile: Int = validFirstTiles.random(random),
        clockwise: Boolean = random.nextBoolean()
    ) {
        check(firstTile in validFirstTiles) { "Invalid first tile: $firstTile" }
        logger.debug { "Generating tile values with first tile: $firstTile and clockwise: $clockwise" }
        boardRotations(firstTile, clockwise).forEach {
            if (this[it].type == Tile.Type.Desert) return@forEach
            this[it]._value = orderOfNumbers.removeFirst()
        }
    }

    private fun generatePortValues() {
        logger.debug { "Generating port values" }
        val ports = ArrayDeque((listOf(*ResourceType.values()) + Collections.nCopies(4, null)).shuffled(random))
        _ports.apply {
            clear()
            var curPort = Port(ports.removeFirst(), size)
            val board = this@Board
            board[0].vertices[Location.TopLeft]!!._port = curPort
            board[0].vertices[Location.Top]!!._port = curPort
            add(curPort)
            curPort = Port(ports.removeFirst(), size)
            board[3].vertices[Location.TopLeft]!!._port = curPort
            board[3].vertices[Location.BottomLeft]!!._port = curPort
            add(curPort)
            curPort = Port(ports.removeFirst(), size)
            board[12].vertices[Location.TopLeft]!!._port = curPort
            board[12].vertices[Location.BottomLeft]!!._port = curPort
            add(curPort)
            curPort = Port(ports.removeFirst(), size)
            board[16].vertices[Location.BottomLeft]!!._port = curPort
            board[16].vertices[Location.Bottom]!!._port = curPort
            add(curPort)
            curPort = Port(ports.removeFirst(), size)
            board[17].vertices[Location.Bottom]!!._port = curPort
            board[17].vertices[Location.BottomRight]!!._port = curPort
            add(curPort)
            curPort = Port(ports.removeFirst(), size)
            board[15].vertices[Location.BottomRight]!!._port = curPort
            board[15].vertices[Location.Bottom]!!._port = curPort
            add(curPort)
            curPort = Port(ports.removeFirst(), size)
            board[11].vertices[Location.BottomRight]!!._port = curPort
            board[11].vertices[Location.TopRight]!!._port = curPort
            add(curPort)
            curPort = Port(ports.removeFirst(), size)
            board[6].vertices[Location.TopRight]!!._port = curPort
            board[6].vertices[Location.Top]!!._port = curPort
            add(curPort)
            curPort = Port(ports.removeFirst(), size)
            board[1].vertices[Location.TopRight]!!._port = curPort
            board[1].vertices[Location.Top]!!._port = curPort
            add(curPort)
        }
    }

    fun debugString(): String = buildString {
        append("Tiles: \n")
        tiles.forEach {
            append("Tile: ${it.id} \n")
            for ((key, value) in it.neighbors) {
                append("\t$key -> ${value.id} \n")
            }
        }
        append("Edges: \n")
        tiles.forEach {
            append("\tFor Tile: ${it.id} \n")
            for ((key, value) in it.edges) {
                append("\t\t$key -> ${if(it.id == value.first.id) value.second?.id else value.first.id} \n")
            }
        }
        append("Vertices: \n")
        vertices.forEach {
            append("\tVertex with (")
            for ((key, value) in it.tiles) {
                append("$key -> ${value.id}, ")
            }
            append(") \n")
        }
    }
}

private val orderOfNumbers = ArrayDeque(listOf(5, 2, 6, 3, 8, 10, 9, 12, 11, 4, 8, 10, 9, 4, 5, 6, 3, 11))

fun generateTiles(random: Random = Random): List<Tile> {
    return Tile.Type.values()
        .flatMap { Collections.nCopies(it.numberOfTiles, it) }.shuffled(random)
        .mapIndexed { index, type -> Tile(index, type) }
}