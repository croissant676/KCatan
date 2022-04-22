/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.core.player

import javafx.scene.paint.Color as JFXColor
import dev.kason.catan.core.board.Vertex

class Player (val id: Int, val color: Color) {
    val settlements: MutableList<Vertex> = mutableListOf()
    val resources = PlayerResourceMap(this)

    @Suppress("MemberVisibilityCanBePrivate")
    enum class Color {
        Red,
        Blue,
        White,
        Orange;

        val colorHex: String
            get() = when (this) {
                Red -> "5aa2ff"
                Blue -> "fafafa"
                White -> "ffb75a"
                Orange -> "ff655a"
            }
        val color: JFXColor get() = JFXColor.web("#${colorHex}")
    }
}