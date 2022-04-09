package dev.kason.kcatan

import com.google.common.io.Resources
import dev.kason.kcatan.ui.CatanStyles
import dev.kason.kcatan.ui.MenuView
import mu.KLogging
import org.slf4j.bridge.SLF4JBridgeHandler
import tornadofx.*

@Suppress("UnstableApiUsage")
fun main(args: Array<String>) {
    Resources.readLines(Resources.getResource("banner.txt"), Charsets.UTF_8).forEach { println(it) }
    SLF4JBridgeHandler.removeHandlersForRootLogger()
    SLF4JBridgeHandler.install()
    launch<CatanApp>(args)
}

class CatanApp : App(MenuView::class, CatanStyles::class) {
    companion object : KLogging()
    override fun init() {
        logger.info("Starting Catan: Loaded TornadoFX application.")
        reloadViewsOnFocus()
        reloadStylesheetsOnFocus()
    }
}