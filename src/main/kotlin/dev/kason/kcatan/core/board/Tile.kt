package dev.kason.kcatan.core.board

import dev.kason.kcatan.core.Resources
import kotlin.math.absoluteValue

class Tile(val type: Type, val id: Int) {
    var value: Int = 0
        internal set
    val neighbors: MutableMap<Location, Tile> = mutableMapOf()
    val edges: MutableMap<Location, Edge> = mutableMapOf()
    val intersections: MutableMap<Location, Intersection> = mutableMapOf()
    val displayDots get() = 6 - (value - 7).absoluteValue

    enum class Type {
        HILLS,
        FOREST,
        MOUNTAINS,
        DESERT,
        FIELDS,
        PASTURE;

        val resource: Resources?
            get() = when (this) {
                HILLS -> Resources.BRICK
                FOREST -> Resources.LUMBER
                MOUNTAINS -> Resources.ORE
                FIELDS -> Resources.GRAIN
                PASTURE -> Resources.WOOL
                DESERT -> null
            }
        val formalName: String
            get() = when (this) {
                HILLS -> "Hills"
                FOREST -> "Forest"
                MOUNTAINS -> "Mountains"
                DESERT -> "Desert"
                FIELDS -> "Fields"
                PASTURE -> "Pasture"
            }
        val numberOfTiles: Int
            get() = when (this) {
                HILLS -> 3
                FOREST -> 4
                MOUNTAINS -> 3
                DESERT -> 1
                FIELDS -> 4
                PASTURE -> 4
            }
    }
}