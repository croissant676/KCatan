package dev.kason.kcatan

import com.google.common.io.Resources
import dev.kason.kcatan.ui.*
import javafx.stage.Stage
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

class CatanApp : App(
    MenuView::class, CatanStyles::class,
    MenuView.Styles::class,
    BoardGenerationView.Styles::class
) {
    companion object : KLogging()

    override fun init() {
        logger.info { "Started TornadoFX Application: CatanApp" }
        reloadViewsOnFocus()
        reloadStylesheetsOnFocus()
    }

    override fun start(stage: Stage) {
        stage.isResizable = false
        super.start(stage)
    }
}