/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.file

import dev.kason.catan.core.Game
import dev.kason.catan.core.board.*
import dev.kason.catan.core.player.*
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class GameRep(
    val players: List<PlayerRep>,
    val boardRep: BoardRep,
    var currentTurn: Int,
    val devCardDeck: List<DevCardType>
) {
    // Ahhh so much work
    fun createGame(name: String): Game {
        @Suppress("LocalVariableName")
        val _players = players.map { it.createPlayer() }
        val game = Game(
            Random,
            players.size,
            players.map { it.color },
            _players,
            name,
            boardRep.run {
                val board = Board(
                    Random,
                    tiles = tiles.mapIndexed { index, tileRep ->
                        Tile(index, tileRep.type).also {
                            it._value = tileRep.number
                        }
                    },
                    init = false
                )
                edges.forEach { edgeRep ->
                    val first = board[edgeRep.tiles.first]
                    val edge = Edge(first)
                    edgeRep.tiles.second?.let { second ->
                        edge.second = board[second]
                    }
                    edge.player = edgeRep.player?.let { _players[it] }
                    board._edges += edge
                }
                board
            }
        )
        return game
    }
}

fun createGameRepFromGame(game: Game) = GameRep(
    game.players.map { createPlayerRepFromPlayer(it) },
    createBoardRep(game.board),
    game.currentPlayerIndex,
    game.developmentCardDeck
)

fun createPlayerRepFromPlayer(player: Player) = PlayerRep(
    player.id,
    player.color,
    ResourceMap(player.resources)
)

@Serializable
class PlayerRep(
    val id: Int,
    val color: Player.Color,
    val resources: ResourceMap,
    val devCards: Map<DevCardType, Int> = mapOf()
) {
    fun createPlayer() = Player(id, color).apply {
        resources.forEach { (type, amount) ->
            this.resources[type] = amount
        }
        developmentCards += devCards
    }
}

// Board rep

internal fun createBoardRep(board: Board): BoardRep {
    val tiles = board.map { createTileRep(it) }
    val edges = board.edges.map { createEdgeRep(it) }
    val vertices = board.vertices.map { createVertexRep(it) }
    val portOrder = board.ports.map { it.resourceType }
    return BoardRep(tiles, edges, vertices, board.robberIndex, portOrder)
}

internal fun createTileRep(tile: Tile): TileRep = TileRep(tile.type, tile.value)
internal fun createEdgeRep(edge: Edge): EdgeRep = EdgeRep(edge.first.id to edge.second?.id, edge.player?.id)
internal fun createVertexRep(vertex: Vertex): VertexRep =
    VertexRep(vertex.player?.id, vertex.isCity, vertex.tiles.mapValues { it.value.id })

@Serializable
class BoardRep(
    val tiles: List<TileRep>,
    val edges: List<EdgeRep>,
    val vertices: List<VertexRep>,
    val robber: Int,
    val portOrder: List<ResourceType?>
)

@Serializable
class TileRep(
    val type: Tile.Type,
    val number: Int,
)

@Serializable
class EdgeRep(
    val tiles: Pair<Int, Int?>,
    val player: Int?
)

@Serializable
class VertexRep(
    val owner: Int?,
    val isCity: Boolean,
    val numbers: LocationMap<Int>
)