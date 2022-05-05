/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core

import dev.kason.catan.core.board.Location
import dev.kason.catan.core.player.ResourceMap
import kotlin.math.sin

object Constants {
    val leftMost = setOf(0, 3, 7, 12, 16)
    val rightMost = setOf(2, 6, 11, 15, 18)

    val roadCost = ResourceMap(brick = 1, lumber = 1)
    val settlementCost = ResourceMap(brick = 1, lumber = 1, wool = 1, grain = 1)
    val cityCost = ResourceMap(ore = 3, grain = 2)
    val developmentCardCost = ResourceMap(ore = 1, grain = 1, wool = 1)
    val sin = sin(20.0)

    const val maxRoads = 15
    const val maxSettlements = 5
    const val maxCities = 4

    val lineTranslations = mapOf(
        Location.TopLeft to LineTransValue(
            startX = -62,
            startY = -66,
            endX = -112,
            endY = -36
        ),
        Location.TopRight to LineTransValue(
            startX = -62,
            startY = -66,
            endX = -9,
            endY = -36
        ),
        Location.Right to LineTransValue(
            startX = -9,
            startY = -36,
            endX = -9,
            endY = 27
        ),
        Location.BottomRight to LineTransValue(
            startX = -9,
            startY = 27,
            endX = -60,
            endY = 57
        ),
        Location.BottomLeft to LineTransValue(
            startX = -112,
            startY = 27,
            endX = -60,
            endY = 57
        ),
        Location.Left to LineTransValue(
            startX = -112,
            startY = -36,
            endX = -112,
            endY = 27
        )
    )

    val pointTranslations = mapOf(
        Location.Top to PointTransValue(
            x = -62,
            y = -66
        ),
        Location.TopLeft to PointTransValue(
            x = -112,
            y = -36
        ),
        Location.TopRight to PointTransValue(
            x = -9,
            y = -36
        ),
        Location.BottomRight to PointTransValue(
            x = -9,
            y = 27
        ),
        Location.Bottom to PointTransValue(
            x = -60,
            y = 57
        ),
        Location.BottomLeft to PointTransValue(
            x = -112,
            y = 27
        )
    )

    const val settlementRadius = 10.0
    const val cityRadius = 20.0

    const val robberRadius = 9.0
    const val maxRepeat = 10
}

class LineTransValue(
    val startX: Int,
    val startY: Int,
    val endX: Int,
    val endY: Int
)

class PointTransValue(
    val x: Int,
    val y: Int
)