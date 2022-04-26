/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core.board

import dev.kason.catan.core.player.Player

@Suppress("PropertyName", "LocalVariableName")
data class Vertex(
    internal val _tiles: LocationMutableMap<Tile> = createLocationMap(),
    var player: Player? = null,
    var isCity: Boolean = false
) {
    val tiles: LocationMap<Tile> = _tiles

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

}