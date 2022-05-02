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
    private val tiles: List<Tile> = generateTiles(random),
    val init: Boolean = true
) : List<Tile> by tiles {

    companion object : KLogging()

    val _edges = mutableSetOf<Edge>()
    val edges by lazy { _edges.sortedBy { it.id } }

    val _vertices = mutableSetOf<Vertex>()
    val vertices by lazy { _vertices.sortedBy { it.id } }

    var robberIndex = tiles.indexOfFirst { it.type == Tile.Type.Desert }
    val robberTile get() = tiles[robberIndex]

    private val _ports = mutableListOf<Port>()
    val ports: List<Port> = _ports

    // ----------------- Initialization -------------------

    init {
        if (init) {
            tileGraph()
            edgeGraph()
            vertexGraph()
            edgesToVertices()
            generateTileValues()
            generatePortValues()
        }
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

    private fun generatePortValues(
        ports: ArrayDeque<ResourceType?> = ArrayDeque(
            (listOf(*ResourceType.values()) + Collections.nCopies(
                4,
                null
            )).shuffled(random)
        )
    ) {
        logger.debug { "Generating port values" }
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

    fun edgesToVertices () {
        edges[0].vertices += vertices[0]
        edges[0].vertices += vertices[1]
        edges[1].vertices += vertices[0]
        edges[1].vertices += vertices[2]
        edges[2].vertices += vertices[1]
        edges[2].vertices += vertices[4]
        edges[3].vertices += vertices[2]
        edges[3].vertices += vertices[5]
        edges[4].vertices += vertices[3]
        edges[4].vertices += vertices[4]
        edges[5].vertices += vertices[3]
        edges[5].vertices += vertices[5]
        edges[6].vertices += vertices[2]
        edges[6].vertices += vertices[6]
        edges[7].vertices += vertices[6]
        edges[7].vertices += vertices[7]
        edges[8].vertices += vertices[7]
        edges[8].vertices += vertices[9]
        edges[9].vertices += vertices[5]
        edges[9].vertices += vertices[8]
        edges[10].vertices += vertices[8]
        edges[10].vertices += vertices[9]
        edges[11].vertices += vertices[7]
        edges[11].vertices += vertices[10]
        edges[12].vertices += vertices[10]
        edges[12].vertices += vertices[11]
        edges[13].vertices += vertices[11]
        edges[13].vertices += vertices[13]
        edges[14].vertices += vertices[9]
        edges[14].vertices += vertices[12]
        edges[15].vertices += vertices[12]
        edges[15].vertices += vertices[13]
        edges[16].vertices += vertices[4]
        edges[16].vertices += vertices[14]
        edges[17].vertices += vertices[14]
        edges[17].vertices += vertices[16]
        edges[18].vertices += vertices[3]
        edges[18].vertices += vertices[17]
        edges[19].vertices += vertices[15]
        edges[19].vertices += vertices[16]
        edges[20].vertices += vertices[15]
        edges[20].vertices += vertices[17]
        edges[21].vertices += vertices[8]
        edges[21].vertices += vertices[19]
        edges[22].vertices += vertices[17]
        edges[22].vertices += vertices[18]
        edges[23].vertices += vertices[18]
        edges[23].vertices += vertices[19]
        edges[24].vertices += vertices[12]
        edges[24].vertices += vertices[21]
        edges[25].vertices += vertices[19]
        edges[25].vertices += vertices[20]
        edges[26].vertices += vertices[20]
        edges[26].vertices += vertices[21]
        edges[27].vertices += vertices[13]
        edges[27].vertices += vertices[22]
        edges[28].vertices += vertices[22]
        edges[28].vertices += vertices[24]
        edges[29].vertices += vertices[21]
        edges[29].vertices += vertices[23]
        edges[30].vertices += vertices[23]
        edges[30].vertices += vertices[24]
        edges[31].vertices += vertices[16]
        edges[31].vertices += vertices[25]
        edges[32].vertices += vertices[25]
        edges[32].vertices += vertices[27]
        edges[33].vertices += vertices[15]
        edges[33].vertices += vertices[28]
        edges[34].vertices += vertices[26]
        edges[34].vertices += vertices[27]
        edges[35].vertices += vertices[26]
        edges[35].vertices += vertices[28]
        edges[36].vertices += vertices[18]
        edges[36].vertices += vertices[30]
        edges[37].vertices += vertices[28]
        edges[37].vertices += vertices[29]
        edges[38].vertices += vertices[29]
        edges[38].vertices += vertices[30]
        edges[39].vertices += vertices[20]
        edges[39].vertices += vertices[32]
        edges[40].vertices += vertices[30]
        edges[40].vertices += vertices[31]
        edges[41].vertices += vertices[31]
        edges[41].vertices += vertices[32]
        edges[42].vertices += vertices[23]
        edges[42].vertices += vertices[34]
        edges[43].vertices += vertices[32]
        edges[43].vertices += vertices[33]
        edges[44].vertices += vertices[33]
        edges[44].vertices += vertices[34]
        edges[45].vertices += vertices[24]
        edges[45].vertices += vertices[35]
        edges[46].vertices += vertices[35]
        edges[46].vertices += vertices[37]
        edges[47].vertices += vertices[34]
        edges[47].vertices += vertices[36]
        edges[48].vertices += vertices[36]
        edges[48].vertices += vertices[37]
        edges[49].vertices += vertices[26]
        edges[49].vertices += vertices[39]
        edges[50].vertices += vertices[29]
        edges[50].vertices += vertices[40]
        edges[51].vertices += vertices[38]
        edges[51].vertices += vertices[39]
        edges[52].vertices += vertices[38]
        edges[52].vertices += vertices[40]
        edges[53].vertices += vertices[31]
        edges[53].vertices += vertices[42]
        edges[54].vertices += vertices[40]
        edges[54].vertices += vertices[41]
        edges[55].vertices += vertices[41]
        edges[55].vertices += vertices[42]
        edges[56].vertices += vertices[33]
        edges[56].vertices += vertices[44]
        edges[57].vertices += vertices[42]
        edges[57].vertices += vertices[43]
        edges[58].vertices += vertices[43]
        edges[58].vertices += vertices[44]
        edges[59].vertices += vertices[36]
        edges[59].vertices += vertices[46]
        edges[60].vertices += vertices[44]
        edges[60].vertices += vertices[45]
        edges[61].vertices += vertices[45]
        edges[61].vertices += vertices[46]
        edges[62].vertices += vertices[38]
        edges[62].vertices += vertices[48]
        edges[63].vertices += vertices[41]
        edges[63].vertices += vertices[49]
        edges[64].vertices += vertices[47]
        edges[64].vertices += vertices[48]
        edges[65].vertices += vertices[47]
        edges[65].vertices += vertices[49]
        edges[66].vertices += vertices[43]
        edges[66].vertices += vertices[51]
        edges[67].vertices += vertices[49]
        edges[67].vertices += vertices[50]
        edges[68].vertices += vertices[50]
        edges[68].vertices += vertices[51]
        edges[69].vertices += vertices[45]
        edges[69].vertices += vertices[53]
        edges[70].vertices += vertices[51]
        edges[70].vertices += vertices[52]
        edges[71].vertices += vertices[52]
        edges[71].vertices += vertices[53]
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