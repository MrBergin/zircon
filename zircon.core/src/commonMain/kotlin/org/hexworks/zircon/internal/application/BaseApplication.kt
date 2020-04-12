package org.hexworks.zircon.internal.application

import kotlinx.coroutines.*
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.application.AppConfig
import org.hexworks.zircon.api.application.Application
import org.hexworks.zircon.api.application.RenderData
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.internal.renderer.Renderer
import org.hexworks.zircon.platform.util.SystemUtils
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.Synchronized

abstract class BaseApplication(
        config: AppConfig,
        override val tileGrid: TileGrid,
        override val coroutineContext: CoroutineContext
) : Application, CoroutineScope {

    abstract val renderer: Renderer

    private val beforeRenderData = RenderData(SystemUtils.getCurrentTimeMs()).toProperty()
    private val afterRenderData = RenderData(SystemUtils.getCurrentTimeMs()).toProperty()

    private val logger = LoggerFactory.getLogger(this::class)
    private val renderInterval = 1000.div(config.fpsLimit)

    private var stopped = false
    private var running = false

    private var paused = false
    private var lastRender = 0L

    @Synchronized
    override fun start() {
        if (stopped.not() && running.not()) {
            renderer.create()
            running = true
            launch {
                while (!stopped && running) {
                    if (paused.not()) {
                        try {
                            val now = SystemUtils.getCurrentTimeMs()
                            if (now - lastRender > renderInterval) {
                                logger.debug(
                                        "Rendering because now: $now - last render: $lastRender > render interval: $renderInterval.")
                                doRender()
                                lastRender = now
                            }
                        } catch (e: Exception) {
                            logger.error("Render failed", e)
                        }
                    }
                }
            }

        }
    }

    override fun pause() {
        paused = true
    }

    override fun resume() {
        paused = false
    }

    @Synchronized
    override fun stop() {
        stopped = true
        running = false
        renderer.close()
        tileGrid.close()
        coroutineContext.cancel()
    }

    override fun beforeRender(listener: (RenderData) -> Unit) = beforeRenderData.onChange {
        listener(it.newValue)
    }

    override fun afterRender(listener: (RenderData) -> Unit) = afterRenderData.onChange {
        listener(it.newValue)
    }

    private suspend fun doRender() {
        beforeRenderData.value = RenderData(SystemUtils.getCurrentTimeMs())
        //TODO fix this to work in multiplatform
        /*if (config.debugMode) {
            RunTimeStats.addTimedStatFor("debug.render.time") {
                renderer.render()
            }
        } else {
            renderer.render()
        }*/
        renderer.render()
        afterRenderData.value = RenderData(SystemUtils.getCurrentTimeMs())
    }
}
