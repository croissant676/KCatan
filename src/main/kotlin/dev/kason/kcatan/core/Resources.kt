package dev.kason.kcatan.core

import dev.kason.kcatan.core.board.Tile
import java.util.EnumMap

enum class Resources {
    LUMBER,
    WOOL,
    GRAIN,
    BRICK,
    ORE;

    val tileType: Tile.Type
        get() = when (this) {
            LUMBER -> Tile.Type.FOREST
            WOOL -> Tile.Type.PASTURE
            GRAIN -> Tile.Type.FIELDS
            BRICK -> Tile.Type.HILLS
            ORE -> Tile.Type.MOUNTAINS
        }

    val formalName: String
        get() = when (this) {
            LUMBER -> "Lumber"
            WOOL -> "Wool"
            GRAIN -> "Grain"
            BRICK -> "Brick"
            ORE -> "Ore"
        }
}

data class SimpleResources(private val map: Map<Resources, Int>) : Map<Resources, Int> by map {
    constructor(
        lumber: Int,
        wool: Int,
        grain: Int,
        brick: Int,
        ore: Int
    ) : this(EnumMap<Resources, Int>(Resources::class.java).also {
        it[Resources.LUMBER] = lumber
        it[Resources.WOOL] = wool
        it[Resources.GRAIN] = grain
        it[Resources.BRICK] = brick
        it[Resources.ORE] = ore
    })
}

data class PlayerResources(private val hashMap: MutableMap<Resources, Int> = EnumMap(Resources::class.java)) :
    MutableMap<Resources, Int> by hashMap {
    constructor(simpleResources: SimpleResources) : this(EnumMap(simpleResources))
    constructor(
        lumber: Int,
        wool: Int,
        grain: Int,
        brick: Int,
        ore: Int
    ) : this(EnumMap<Resources, Int>(Resources::class.java).also {
        it[Resources.LUMBER] = lumber
        it[Resources.WOOL] = wool
        it[Resources.GRAIN] = grain
        it[Resources.BRICK] = brick
        it[Resources.ORE] = ore
    })
}

operator fun Map<Resources, Int>.minus(map: Map<Resources, Int>): Map<Resources, Int> {
    val result = EnumMap<Resources, Int>(Resources::class.java)
    for (resource in Resources.values()) {
        result[resource] = getOrDefault(resource, 0) - map.getOrDefault(resource, 0)
    }
    return result
}