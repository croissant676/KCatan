/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core.player

enum class DevCardType {
    VictoryPoint,
    YearOfPlenty,
    RoadBuilding,
    Monopoly,
    Knight;

    val numberOfCards: Int get() = when (this) {
        VictoryPoint -> 5
        Knight -> 14
        else -> 2
    }
}

