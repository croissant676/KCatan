/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core.board

import dev.kason.catan.core.player.ResourceType

@Suppress("PropertyName")
data class Tile(
    val id: Int,
    val type: Type,
) {
    internal val _neighbors: LocationMutableMap<Tile> = createLocationMap()
    internal val _edges: LocationMutableMap<Edge> = createLocationMap()
    internal val _vertices: LocationMutableMap<Vertex> = createLocationMap()
    val neighbors: LocationMap<Tile> = _neighbors
    val edges: LocationMap<Edge> = _edges
    val vertices: LocationMap<Vertex> = _vertices

    enum class Type {
        Hills,
        Forest,
        Mountains,
        Desert,
        Fields,
        Pasture;
        val resource: ResourceType?
            get() = when (this) {
                Hills -> ResourceType.Brick
                Forest -> ResourceType.Lumber
                Mountains -> ResourceType.Ore
                Fields -> ResourceType.Grain
                Pasture -> ResourceType.Wool
                Desert -> null
            }
        val numberOfTiles: Int
            get() = when (this) {
                Desert -> 1
                Hills, Mountains -> 3
                else -> 4
            }
    }
}