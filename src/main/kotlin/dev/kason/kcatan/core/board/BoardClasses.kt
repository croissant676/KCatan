package dev.kason.kcatan.core.board

import dev.kason.kcatan.core.player.Player

class Intersection(val tiles: MutableList<Tile> = mutableListOf()) : List<Tile> by tiles {
    var player: Player? = null
    var isCity: Boolean = false
    val hasConstruction get() = player != null
    val isSettlement get() = player != null && !isCity
}


@Suppress("MemberVisibilityCanBePrivate")
class Edge(
    /** First is always the tile to the top, and in the case that the edge is vertical, it is the left tile. */
    val first: Tile
) {
    var player: Player? = null
    var second: Tile? = null
        private set
    val tiles: List<Tile>
        get() = listOfNotNull(first, second)
    val hasRoad: Boolean get() = player != null
    operator fun plusAssign(tile: Tile) =
        if (second == null) second = tile else Unit
}