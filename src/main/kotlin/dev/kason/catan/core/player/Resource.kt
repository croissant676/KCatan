/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

@file:Suppress("MemberVisibilityCanBePrivate")

package dev.kason.catan.core.player

import dev.kason.catan.core.board.Tile
import java.util.EnumMap
import jdk.internal.loader.Resource

enum class ResourceType {
    Lumber,
    Wool,
    Grain,
    Brick,
    Ore;

    val producer: Tile.Type
        get() = when (this) {
            Lumber -> Tile.Type.Forest
            Wool -> Tile.Type.Pasture
            Grain -> Tile.Type.Fields
            Brick -> Tile.Type.Hills
            Ore -> Tile.Type.Mountains
        }
}

data class PlayerResourceMap(
    val player: Player,
    val resources: MutableMap<ResourceType, Int> = EnumMap(ResourceType::class.java)
) : MutableMap<ResourceType, Int> by resources

data class ResourceMap(val resources: Map<ResourceType, Int>) : Map<ResourceType, Int> by resources {
    constructor(
        lumber: Int = 0,
        wool: Int = 0,
        grain: Int = 0,
        brick: Int = 0,
        ore: Int = 0
    ) : this(EnumMap<ResourceType, Int>(ResourceType::class.java).apply {
        this += mapOf(
            ResourceType.Lumber to lumber,
            ResourceType.Wool to wool,
            ResourceType.Grain to grain,
            ResourceType.Brick to brick,
            ResourceType.Ore to ore
        )
    })
}

val Map<ResourceType, Int>.lumber: Int
    get() = this[ResourceType.Lumber] ?: 0
val Map<ResourceType, Int>.wool: Int
    get() = this[ResourceType.Wool] ?: 0
val Map<ResourceType, Int>.grain: Int
    get() = this[ResourceType.Grain] ?: 0
val Map<ResourceType, Int>.brick: Int
    get() = this[ResourceType.Brick] ?: 0
val Map<ResourceType, Int>.ore: Int
    get() = this[ResourceType.Ore] ?: 0

operator fun Map<ResourceType, Int>.plus(other: Map<ResourceType, Int>): ResourceMap {
    val map = EnumMap<ResourceType, Int>(ResourceType::class.java)
    for (key in this.keys) {
        map[key] = this[key]!! + other[key]!!
    }
    return ResourceMap(map)
}

operator fun Map<ResourceType, Int>.minus(other: Map<ResourceType, Int>): ResourceMap {
    val map = EnumMap<ResourceType, Int>(ResourceType::class.java)
    for (key in this.keys) {
        map[key] = this[key]!! - other[key]!!
    }
    return ResourceMap(map)
}

operator fun Map<ResourceType, Int>.times(other: Int): ResourceMap {
    val map = EnumMap<ResourceType, Int>(ResourceType::class.java)
    for (key in this.keys) {
        map[key] = this[key]!! * other
    }
    return ResourceMap(map)
}

operator fun PlayerResourceMap.plusAssign(other: Map<ResourceType, Int>) {
    for (key in other.keys) {
        this[key] = this[key]!! + other[key]!!
    }
}

operator fun PlayerResourceMap.minusAssign(other: Map<ResourceType, Int>) {
    for (key in other.keys) {
        this[key] = this[key]!! - other[key]!!
    }
}

infix fun Map<ResourceType, Int>.has(other: Map<ResourceType, Int>): Boolean {
    for (key in this.keys) {
        if (this[key]!! < other[key]!!) {
            return false
        }
    }
    return true
}

infix fun Map<ResourceType, Int>.doesNotHave(other: Map<ResourceType, Int>): Boolean {
    for (key in this.keys) {
        if (this[key]!! < other[key]!!) {
            return true
        }
    }
    return false
}