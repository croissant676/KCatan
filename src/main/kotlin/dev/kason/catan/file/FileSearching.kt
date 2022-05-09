/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan.file

import com.sun.security.auth.module.NTSystem
import dev.kason.catan.core.Game
import java.io.File
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging

val fileJson = Json {
    prettyPrint = true
}

private val username = NTSystem().name

val readmeText = """
    This folder contains all the game files (ie, previous games).
    Unless you know what you are doing, please refrain from editing or touching the files.
    If you do, you may lose progress or cause unexpected errors.
    
    The files are named according to the game name, and are in JSON format.
    If you have an understanding about how JSON files work, and how to use them,
    you can read the file by opening it in a text editor. Still, it is recommended
    to avoid editing the files.
    
    Important:
    
    Encoding an game instance to a json and then deserializing it back to an game instance
    changes some minor properties. For instance, the random is changed to the default KT random.
    Although this should do very little to affect gameplay, it demonstrates some 
    unexpected behaviors that may emerge. Overall, the retrieved game should be almost identical
    to the one saved.
    
    If you have any questions, please contact the developer.
""".trimIndent()

private val fileSearchingLogger = KotlinLogging.logger { }

fun listAllFiles(): List<File> {
    val name = NTSystem().name
    val file = File("C:\\Users\\$name\\AppData\\Local\\Catan\\games")
    if (!file.exists()) {
        file.mkdirs()
        val readMe = File(file, "README.txt")
        readMe.writeText(readmeText)
        fileSearchingLogger.info { "Created Catan folder & README.txt." }
        return emptyList()
    }
    // We only care about the json files.
    return file.listFiles()?.toList()?.filter { it.extension == "json" } ?: emptyList()
}

fun loadFileJson(fileName: File): Game? {
    val files = listAllFiles()
    if (fileName !in files) return null
    return fileJson.decodeFromString<GameRep>(fileName.readText()).createGame(fileName.nameWithoutExtension)
}

fun loadFileJson(name: String): Game? = loadFileJson(File("C:\\Users\\$username\\AppData\\Local\\Catan\\games\\${name}.json"))

fun Game.saveJson(): File? = kotlin.runCatching {
    val file = File("C:\\Users\\$username\\AppData\\Local\\Catan\\games\\${gameName}.json")
    if (!file.exists()) {
        file.createNewFile()
    }
    file.writeText(fileJson.encodeToString(createGameRepFromGame(this)))
    file
}.getOrNull()