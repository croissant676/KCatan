/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core.board

import dev.kason.catan.core.game
import dev.kason.catan.core.player.Player
import dev.kason.catan.core.player.ResourceType

private var currentVertexNum = 0

@Suppress("PropertyName", "LocalVariableName")
data class Vertex(
    internal val _tiles: LocationMutableMap<Tile> = createLocationMap(),
    var player: Player? = null,
    var isCity: Boolean = false,
    val vertexNum: Int = currentVertexNum++
) {
    val tiles: LocationMap<Tile> = _tiles
    internal var _port: Port? = null
    val port: Port? = _port
    val hasPort: Boolean get() = _port != null
    val rotation: Rotation by lazy {
        val locations = tiles.keys
        if (Location.Bottom in locations || Location.TopLeft in locations || Location.TopRight in locations) {
            Rotation.Top
        }
        Rotation.Bottom
    }

    val edges: List<Edge> by lazy {
        val _edges = mutableSetOf<Edge>()
        if(rotation == Rotation.Top) {
            val tileKeys = tiles.keys
            if(Location.TopLeft in tileKeys) {
                val tile = tiles[Location.TopLeft]!!
                _edges += tile.edges[Location.Right]!!
                _edges += tile.edges[Location.BottomRight]!!
            }
            if(Location.Bottom in tileKeys) {
                val tile = tiles[Location.Bottom]!!
                _edges += tile.edges[Location.TopLeft]!!
                _edges += tile.edges[Location.TopRight]!!
            }
            if(Location.TopRight in tileKeys) {
                val tile = tiles[Location.TopRight]!!
                _edges += tile.edges[Location.BottomLeft]!!
                _edges += tile.edges[Location.Left]!!
            }
        } else {
            val tileKeys = tiles.keys
            if(Location.Top in tileKeys) {
                val tile = tiles[Location.Top]!!
                _edges += tile.edges[Location.BottomLeft]!!
                _edges += tile.edges[Location.BottomRight]!!
            }
            if(Location.BottomLeft in tileKeys) {
                val tile = tiles[Location.BottomLeft]!!
                _edges += tile.edges[Location.TopRight]!!
                _edges += tile.edges[Location.Right]!!
            }
            if(Location.BottomRight in tileKeys) {
                val tile = tiles[Location.BottomRight]!!
                _edges += tile.edges[Location.TopLeft]!!
                _edges += tile.edges[Location.Left]!!
            }
        }
        _edges.toList()
    }

    val vertices: List<Vertex> by lazy {
        val _vertices = mutableSetOf<Vertex>()
        edges.flatMapTo(_vertices) { it.vertices }
        _vertices.toList()
    }

    val hasConstruction: Boolean get() = player != null
    val isEmpty: Boolean get() = player == null
    val isSettlement: Boolean get() = player != null && !isCity

    enum class Rotation { Top, Bottom }

    override fun equals(other: Any?): Boolean = other is Vertex && other.vertexNum == vertexNum
    override fun hashCode(): Int = vertexNum
    override fun toString(): String {
        return "Vertex(player=$player, vertexNum=$vertexNum)"
    }
}

data class Port(
    val resourceType: ResourceType?,
    val id: Int
) {
    val tiles by lazy { game.board.vertices.filter { this == it.port } }
    val formalName: String get() = resourceType?.name ?: "Generic"
    val cssName: String get() = resourceType?.producer?.name?.lowercase() ?: "generic-port"
    val description: String
        get() = if (resourceType == null) "Trades anything for anything at a 3 : 1 ratio." else
            "Trades ${resourceType.name} for anything at a 2 : 1 ratio."
}