/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core.board

import dev.kason.catan.core.player.Player

private var currentEdgeNumber = 0

data class Edge(val first: Tile, val id: Int = currentEdgeNumber++) {
    var second: Tile? = null
        internal set
    var player: Player? = null
    val isEmpty get() = player == null
    val vertices: List<Vertex> by lazy {
        first.vertices.values.filter { this in it.edges }
    }
    val isPort = false
}