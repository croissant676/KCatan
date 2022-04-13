package dev.kason.kcatan.core

import dev.kason.kcatan.core.board.Tile
import dev.kason.kcatan.core.player.Player
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
        lumber: Int = 0,
        wool: Int = 0,
        grain: Int = 0,
        brick: Int = 0,
        ore: Int = 0
    ): this(EnumMap<Resources, Int>(Resources::class.java).also {
        it[Resources.LUMBER] = lumber
        it[Resources.WOOL] = wool
        it[Resources.GRAIN] = grain
        it[Resources.BRICK] = brick
        it[Resources.ORE] = ore
    })
}

data class PlayerResources(
    private val player: Player,
    private val hashMap: MutableMap<Resources, Int> = EnumMap(Resources::class.java)
) : MutableMap<Resources, Int> by hashMap {
    constructor(player: Player, simpleResources: SimpleResources) : this(player, EnumMap(simpleResources))
    constructor(
        player: Player,
        lumber: Int,
        wool: Int,
        grain: Int,
        brick: Int,
        ore: Int
    ) : this(player, EnumMap<Resources, Int>(Resources::class.java).also {
        it[Resources.LUMBER] = lumber
        it[Resources.WOOL] = wool
        it[Resources.GRAIN] = grain
        it[Resources.BRICK] = brick
        it[Resources.ORE] = ore
    })
}

fun Map<Resources, Int>.hasResources(map: Map<Resources, Int>): Boolean {
    return map.all { (resource, amount) ->
        this[resource]?.let { it >= amount } ?: false
    }
}

operator fun Map<Resources, Int>.minus(map: Map<Resources, Int>): Map<Resources, Int> {
    val result = EnumMap<Resources, Int>(Resources::class.java)
    for (resource in Resources.values()) {
        result[resource] = getOrDefault(resource, 0) - map.getOrDefault(resource, 0)
    }
    return result
}

operator fun MutableMap<Resources, Int>.minusAssign(map: Map<Resources, Int>) {
    for (resource in Resources.values()) {
        this[resource] = getOrDefault(resource, 0) - map.getOrDefault(resource, 0)
    }
}