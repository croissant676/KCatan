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
        val _edges = mutableListOf<Edge>()
        _edges
    }

    val hasConstruction: Boolean get() = player != null
    val isEmpty: Boolean get() = player == null
    val isSettlement: Boolean get() = player != null && !isCity

    enum class Rotation { Top, Bottom }

    override fun equals(other: Any?): Boolean = other is Vertex && other.vertexNum == vertexNum
    override fun hashCode(): Int = vertexNum
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