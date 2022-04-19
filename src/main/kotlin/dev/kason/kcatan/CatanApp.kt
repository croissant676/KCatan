package dev.kason.kcatan

import com.google.common.io.Resources
import dev.kason.kcatan.ui.*
import javafx.scene.paint.*
import javafx.stage.Stage
import mu.KLogging
import org.slf4j.bridge.SLF4JBridgeHandler
import tornadofx.*

@Suppress("UnstableApiUsage")
fun main(args: Array<String>) {
    Resources.readLines(Resources.getResource("banner.txt"), Charsets.UTF_8).forEach(::println)
    SLF4JBridgeHandler.removeHandlersForRootLogger()
    SLF4JBridgeHandler.install()
    RadialGradient(
        90.0,
        0.0,
        300.0,
        300.0,
        350.0,
        false,
        CycleMethod.REFLECT,
        listOf(
            Stop(0.0, c("ffeccd")),
            Stop(0.25, c("ffcf80")),
            Stop(0.45, c("ffaf80")),
            Stop(0.6, c("ffaea4")),
            Stop(0.8, c("ffa4c8"))
        )
    ).also { println(it) }
    launch<CatanApp>(args)
}

class CatanApp : App(
    MenuController::class
) {
    companion object : KLogging()

    override fun init() {
        logger.info { "Started TornadoFX Application: CatanApp" }
        reloadViewsOnFocus()
        reloadStylesheetsOnFocus()
        dumpStylesheets()
    }

    override fun start(stage: Stage) {
        stage.isResizable = false
        super.start(stage)
    }
}