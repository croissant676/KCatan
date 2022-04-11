package dev.kason.kcatan.core.board

import java.util.EnumSet
import kotlin.random.Random


private val leftMost = intArrayOf(0, 3, 7, 12, 16)
private val rightMost = intArrayOf(2, 6, 11, 15, 18)
private val edgeTileLocations = EnumSet.of(
    Location.TOP_LEFT,
    Location.TOP_RIGHT,
    Location.BOTTOM_LEFT,
    Location.BOTTOM_RIGHT,
    Location.RIGHT,
    Location.LEFT
)
private val intersectionLocations = EnumSet.of(
    Location.TOP,
    Location.TOP_LEFT,
    Location.BOTTOM_LEFT,
    Location.BOTTOM,
    Location.BOTTOM_RIGHT,
    Location.TOP_RIGHT
)

@Suppress("MemberVisibilityCanBePrivate")
class Board(random: Random = Random, generationArgs: Pair<Int, Boolean> = 1 to false) {
    val tiles = mutableListOf<Tile>()
    val edges = mutableListOf<Edge>()
    val intersections = mutableListOf<Intersection>()

    init {
        val tileTypes = Tile.Type.values()
            .flatMap { type -> List(type.numberOfTiles) { type } }
        tileTypes.shuffled(random).forEachIndexed { index, type -> tiles.add(Tile(type, index)) }
        generateGraph()
        // Generate edges
        tiles.forEach { tile ->
            val neighbors = tile.neighbors
            val keys = neighbors.keys
            fun check(location: Location) {
                if (location !in keys) return
                val edge = neighbors[location]!!.edges[location.opposite]!!
                tile.edges[location] = edge
                edge += tile
            }
            check(Location.TOP_LEFT)
            check(Location.TOP_RIGHT)
            check(Location.LEFT)
            edgeTileLocations.forEach { location ->
                if (location !in tile.edges.keys)
                    tile.edges[location] = Edge(tile)
            }
            edges += tile.edges.values
        }
        // Generate intersections
        tiles.forEach { tile ->
            val intersections = tile.intersections
            val neighborKeys = tile.neighbors.keys
            if (Location.TOP_LEFT in neighborKeys) {
                val topLeft = tile.neighbors[Location.TOP_LEFT]!!
                intersections[Location.TOP_LEFT] = topLeft.intersections[Location.BOTTOM]!!
                intersections[Location.TOP] = topLeft.intersections[Location.BOTTOM_RIGHT]!!
                if (Location.TOP_RIGHT in neighborKeys) {
                    val topRight = tile.neighbors[Location.TOP_RIGHT]!!
                    intersections[Location.TOP_RIGHT] = topRight.intersections[Location.BOTTOM]!!
                }
                if (Location.LEFT in neighborKeys) {
                    val left = tile.neighbors[Location.LEFT]!!
                    intersections[Location.BOTTOM_LEFT] = left.intersections[Location.BOTTOM_RIGHT]!!
                }
            } else if (Location.LEFT in neighborKeys) {
                val left = tile.neighbors[Location.LEFT]!!
                intersections[Location.BOTTOM_LEFT] = left.intersections[Location.BOTTOM_RIGHT]!!
                intersections[Location.TOP_LEFT] = left.intersections[Location.TOP_RIGHT]!!
            } else if (Location.TOP_RIGHT in neighborKeys) {
                val topRight = tile.neighbors[Location.TOP_RIGHT]!!
                intersections[Location.TOP_RIGHT] = topRight.intersections[Location.BOTTOM]!!
                intersections[Location.TOP] = topRight.intersections[Location.BOTTOM_LEFT]!!
            }
            intersectionLocations.forEach {
                if (it !in intersections.keys)
                    intersections[it] = Intersection(mutableListOf(tile))
                else intersections[it]!!.tiles += tile
                this.intersections += intersections[it]!!
            }
        }
        // Generate tile values
        val (value, isClockwise) = generationArgs
        val generationStrategy = ArrayDeque(generateStrategyProvider(value, isClockwise))
        tiles.forEach {
            if (it.type != Tile.Type.DESERT) it.value = generationStrategy.removeFirst()
        }
    }

    private fun generateGraph() = tiles.forEach {
        val neighbors = it.neighbors
        if (it.id !in leftMost) neighbors[Location.LEFT] = tiles[it.id - 1]
        if (it.id !in rightMost) neighbors[Location.RIGHT] = tiles[it.id + 1]
        //<editor-fold desc="Generating graph">
        when (it.id) {
            0, 1, 2 -> {
                neighbors[Location.BOTTOM_LEFT] = tiles[it.id + 3]
                neighbors[Location.BOTTOM_RIGHT] = tiles[it.id + 4]
            }
            3, 4, 5, 6 -> {
                if (it.id != 3) neighbors[Location.TOP_LEFT] = tiles[it.id - 4]
                if (it.id != 6) neighbors[Location.TOP_RIGHT] = tiles[it.id - 3]
                neighbors[Location.BOTTOM_LEFT] = tiles[it.id + 4]
                neighbors[Location.BOTTOM_RIGHT] = tiles[it.id + 5]
            }
            7, 8, 9, 10, 11 -> {
                if (it.id != 7) {
                    neighbors[Location.TOP_LEFT] = tiles[it.id - 5]
                    neighbors[Location.BOTTOM_LEFT] = tiles[it.id + 4]
                }
                if (it.id != 11) {
                    neighbors[Location.TOP_RIGHT] = tiles[it.id - 4]
                    neighbors[Location.BOTTOM_RIGHT] = tiles[it.id + 5]
                }
            }
            12, 13, 14, 15 -> {
                if (it.id != 12) neighbors[Location.BOTTOM_LEFT] = tiles[it.id + 3]
                if (it.id != 15) neighbors[Location.BOTTOM_RIGHT] = tiles[it.id + 4]
                neighbors[Location.TOP_LEFT] = tiles[it.id - 5]
                neighbors[Location.TOP_RIGHT] = tiles[it.id - 4]
            }
            else -> {
                neighbors[Location.TOP_LEFT] = tiles[it.id - 4]
                neighbors[Location.TOP_RIGHT] = tiles[it.id - 3]
            }
        }
        //</editor-fold>
    }
}

fun generateStrategyProvider(firstTile: Int, isClockwise: Boolean): List<Int> =
    if (isClockwise) when (firstTile) {
        0 -> listOf(0, 1, 2, 6, 11, 15, 18, 17, 16, 12, 7, 3, 4, 5, 10, 14, 13, 8, 9)
        1 -> listOf(1, 2, 6, 11, 15, 18, 17, 16, 12, 7, 3, 0, 4, 5, 10, 14, 13, 8, 9)
        2 -> listOf(2, 6, 11, 15, 18, 17, 16, 12, 7, 3, 0, 1, 5, 10, 14, 13, 8, 4, 9)
        3 -> listOf(3, 0, 1, 2, 6, 11, 15, 18, 17, 16, 12, 7, 8, 4, 5, 10, 14, 13, 9)
        6 -> listOf(6, 11, 15, 18, 17, 16, 12, 7, 3, 0, 1, 2, 5, 10, 14, 13, 8, 4, 9)
        7 -> listOf(7, 3, 0, 1, 2, 6, 11, 15, 18, 17, 16, 12, 8, 4, 5, 10, 14, 13, 9)
        11 -> listOf(11, 15, 18, 17, 16, 12, 7, 3, 0, 1, 2, 6, 10, 14, 13, 8, 4, 5, 9)
        12 -> listOf(12, 7, 3, 0, 1, 2, 6, 11, 15, 18, 17, 16, 13, 8, 4, 5, 10, 14, 9)
        15 -> listOf(15, 18, 17, 16, 12, 7, 3, 0, 1, 2, 6, 11, 10, 14, 13, 8, 4, 5, 9)
        16 -> listOf(16, 12, 7, 3, 0, 1, 2, 6, 11, 15, 18, 17, 13, 8, 4, 5, 10, 14, 9)
        17 -> listOf(17, 16, 12, 7, 3, 0, 1, 2, 6, 11, 15, 18, 14, 13, 8, 4, 5, 10, 9)
        18 -> listOf(18, 17, 16, 12, 7, 3, 0, 1, 2, 6, 11, 15, 14, 13, 8, 4, 5, 10, 9)
        else -> throw IllegalStateException("Unexpected value: $firstTile");
    } else when (firstTile) {
        0 -> listOf(0, 3, 7, 12, 16, 17, 18, 15, 11, 6, 2, 1, 4, 8, 13, 14, 10, 5, 9)
        1 -> listOf(1, 0, 3, 7, 12, 16, 17, 18, 15, 11, 6, 2, 5, 4, 8, 13, 14, 10, 9)
        2 -> listOf(2, 1, 0, 3, 7, 12, 16, 17, 18, 15, 11, 6, 5, 4, 8, 13, 14, 10, 9)
        3 -> listOf(3, 7, 12, 16, 17, 18, 15, 11, 6, 2, 1, 0, 4, 8, 13, 14, 10, 5, 9)
        6 -> listOf(6, 2, 1, 0, 3, 7, 12, 16, 17, 18, 15, 11, 10, 5, 4, 8, 13, 14, 9)
        7 -> listOf(7, 12, 16, 17, 18, 15, 11, 6, 2, 1, 0, 3, 8, 13, 14, 10, 5, 4, 9)
        11 -> listOf(11, 6, 2, 1, 0, 3, 7, 12, 16, 17, 18, 15, 10, 5, 4, 8, 13, 14, 9)
        12 -> listOf(12, 16, 17, 18, 15, 11, 6, 2, 1, 0, 3, 7, 8, 13, 14, 10, 5, 4, 9)
        15 -> listOf(15, 11, 6, 2, 1, 0, 3, 7, 12, 16, 17, 18, 14, 10, 5, 4, 8, 13, 9)
        16 -> listOf(16, 17, 18, 15, 11, 6, 2, 1, 0, 3, 7, 12, 13, 14, 10, 5, 4, 8, 9)
        17 -> listOf(17, 18, 15, 11, 6, 2, 1, 0, 3, 7, 12, 16, 13, 14, 10, 5, 4, 8, 9)
        18 -> listOf(18, 15, 11, 6, 2, 1, 0, 3, 7, 12, 16, 17, 14, 10, 5, 4, 8, 13, 9)
        else -> throw IllegalStateException("Unexpected value: $firstTile");
    }


enum class Location {
    TOP,
    TOP_LEFT,
    TOP_RIGHT,
    LEFT,
    RIGHT,
    BOTTOM,
    BOTTOM_LEFT,
    BOTTOM_RIGHT;

    val opposite: Location
        get() = when (this) {
            TOP -> BOTTOM
            TOP_LEFT -> BOTTOM_RIGHT
            TOP_RIGHT -> BOTTOM_LEFT
            LEFT -> RIGHT
            RIGHT -> LEFT
            BOTTOM -> TOP
            BOTTOM_LEFT -> TOP_RIGHT
            BOTTOM_RIGHT -> TOP_LEFT
        }
}