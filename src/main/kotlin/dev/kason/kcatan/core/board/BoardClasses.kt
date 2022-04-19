package dev.kason.kcatan.core.board

import dev.kason.kcatan.core.player.Player

class Intersection(val tiles: MutableList<Tile> = mutableListOf()) : List<Tile> by tiles {
    var player: Player? = null
    var isCity: Boolean = false
    val hasConstruction get() = player != null
    val isSettlement get() = player != null && !isCity
}

@Suppress("MemberVisibilityCanBePrivate")
class Edge(val first: Tile) {
    var second: Tile? = null
    var player: Player? = null
    val hasRoad: Boolean get() = player != null
}