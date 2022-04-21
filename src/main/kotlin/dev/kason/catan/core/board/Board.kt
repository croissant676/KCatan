/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core.board

import dev.kason.catan.core.Constants
import java.util.Collections
import kotlin.random.Random

class Board(
    private val random: Random
) {

    val tiles by lazy {
        val types = Tile.Type.values()
            .flatMap { Collections.nCopies(it.numberOfTiles, it) }.shuffled(random)
        List(19) {
            Tile(it, types[it])
        }
    }

    private val _edges = mutableListOf<Edge>()
    val edges by lazy { _edges.toList() }

    private val _vertices = mutableListOf<Vertex>()
    val vertices by lazy { _vertices.toList() }

    var robberIndex = tiles.indexOfFirst { it.type == Tile.Type.Desert }
    val robberTile get() = tiles[robberIndex]



    // ----------------- Initialization -------------------

    init {
        tileGraph()
        edgeGraph()
        vertexGraph()
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
    }
}

