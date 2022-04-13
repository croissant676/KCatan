package dev.kason.kcatan.core.player

import dev.kason.kcatan.core.SimpleResources

object Costs {
    val road = SimpleResources(brick = 1, lumber = 1)
    val settlement = SimpleResources(brick = 1, lumber = 1, wool = 1, grain = 1)
    val city = SimpleResources(ore = 3, grain = 2)
    val developmentCard = SimpleResources(ore = 1, grain = 1, wool = 1)
}