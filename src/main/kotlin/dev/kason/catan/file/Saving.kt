/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.file

import dev.kason.catan.catanAlert
import dev.kason.catan.core.Game
import dev.kason.catan.core.game
import kotlin.system.exitProcess

fun exitAndSave(saveGame: Game = game): Nothing {
    val files = listAllFiles()
    if (files.any { it.name == game.gameName && it.extension == "json" }) {
        catanAlert(
            "Could not save game",
            "This game cannot be saved because a file with the same name already exists."
        )
        exitProcess(0)
    }
    saveGame.saveJson()
    exitProcess(0)
}