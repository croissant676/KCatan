/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core

import dev.kason.catan.core.player.ResourceMap

object Constants {
    val leftMost = setOf(0, 3, 7, 12, 16)
    val rightMost = setOf(2, 6, 11, 15, 18)

    val roadCost = ResourceMap(brick = 1, lumber = 1) + ResourceMap(brick = 1)
    val settlementCost = ResourceMap(brick = 1, lumber = 1, wool = 1, grain = 1)
    val cityCost = ResourceMap(ore = 3, grain = 2)
    val developmentCardCost = ResourceMap(ore = 1, grain = 1, wool = 1)
}