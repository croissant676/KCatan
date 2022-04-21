/*
 * Copyright (C) 2022 Kason Gu, Michael Xiang, Manas Pathak, Ashley Yang.
 * Use of this code is governed by the MIT Licensed, which can be obtained at:
 * https://opensource.org/licenses/MIT
 */

package dev.kason.catan

import com.google.common.io.Resources.getResource
import com.google.common.io.Resources.readLines
import dev.kason.catan.ui.MenuView
import org.slf4j.bridge.SLF4JBridgeHandler
import tornadofx.App
import tornadofx.launch

@Suppress("UnstableApiUsage")
fun main(args: Array<String>) {
    readLines(getResource("banner.txt"), Charsets.UTF_8).forEach { println(it) }
    SLF4JBridgeHandler.removeHandlersForRootLogger()
    SLF4JBridgeHandler.install()
    launch<CatanApp>(args)
}

class CatanApp: App(MenuView::class)