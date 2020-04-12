package org.hexworks.zircon.internal.application

import kotlinx.coroutines.*
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.application.AppConfig
import org.hexworks.zircon.api.application.Application
import org.hexworks.zircon.api.application.RenderData
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.uievent.*
import org.hexworks.zircon.internal.data.PixelPosition
import org.hexworks.zircon.internal.grid.InternalTileGrid
import org.hexworks.zircon.internal.grid.ThreadSafeTileGrid
import org.hexworks.zircon.internal.renderer.CanvasRenderer
import org.hexworks.zircon.platform.util.SystemUtils
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.Synchronized

class CanvasApplication(appConfig: AppConfig,
                        override val tileGrid: InternalTileGrid = ThreadSafeTileGrid(
                                initialTileset = appConfig.defaultTileset,
                                initialSize = appConfig.size
                        ),
                        canvas: HTMLCanvasElement

) : Application, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + SupervisorJob()
    private val renderer = CanvasRenderer(appConfig, tileGrid, canvas)

    private val beforeRenderData = RenderData(SystemUtils.getCurrentTimeMs()).toProperty()
    private val afterRenderData = RenderData(SystemUtils.getCurrentTimeMs()).toProperty()

    private val logger = LoggerFactory.getLogger(this::class)
    private val renderInterval = 1000.div(appConfig.fpsLimit)

    private var stopped = false
    private var running = false

    private var paused = false
    private var lastRender = 0L

    fun callback(d: Double) {
        launch {
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
            window.requestAnimationFrame(::callback)
        }
    }

    @Synchronized
    override fun start() {
        if (stopped.not() && running.not()) {
            renderer.create()
            running = true
            window.requestAnimationFrame(::callback)
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

class KeyboardEventListener(canvas: HTMLCanvasElement) {

    private val events = mutableListOf<Pair<KeyboardEvent, UIEventPhase>>()

    init {
        fun register(event: String, eventType: KeyboardEventType) {
            canvas.addEventListener(event, { e ->
                if (e !is org.w3c.dom.events.KeyboardEvent) return@addEventListener

                events.add(createKeyboardEvent(e, eventType) to UIEventPhase.TARGET)
            })
        }
        register("keyup", KeyboardEventType.KEY_RELEASED)
        register("keydown", KeyboardEventType.KEY_PRESSED)
        register("keypress", KeyboardEventType.KEY_TYPED)
    }

    fun drainEvents(): Iterable<Pair<KeyboardEvent, UIEventPhase>> {
        return events.toList().also {
            events.clear()
        }
    }

    private fun createKeyboardEvent(e: org.w3c.dom.events.KeyboardEvent, type: KeyboardEventType): KeyboardEvent {
        val keyChar = e.key
        val keyCode = e.keyCode
        val ctrlDown = e.ctrlKey
        val altDown = e.altKey
        val metaDown = e.metaKey
        val shiftDown = e.shiftKey

        return KeyboardEvent(
                type = type,
                key = keyChar,
                code = KeyCode.findByCode(keyCode),
                ctrlDown = ctrlDown,
                altDown = altDown,
                metaDown = metaDown,
                shiftDown = shiftDown)
    }
}


open class MouseEventListener(canvas: HTMLCanvasElement, private val fontWidth: Int, private val fontHeight: Int) {

    private val events = mutableListOf<Pair<MouseEvent, UIEventPhase>>()

    private var mouseIsPressed = false

    init {
        fun register(event: String, eventType: MouseEventType) {
            canvas.addEventListener(event, { e ->
                if (e !is org.w3c.dom.events.MouseEvent) return@addEventListener
                events.add(createMouseEvent(e, eventType) to UIEventPhase.TARGET)
            })
        }
        register("click", MouseEventType.MOUSE_CLICKED)
        register("mousemove", MouseEventType.MOUSE_MOVED)
        register("mouseenter", MouseEventType.MOUSE_ENTERED)
        register("mouseleave", MouseEventType.MOUSE_EXITED)
        register("mouseout", MouseEventType.MOUSE_EXITED)

        canvas.addEventListener("mouseup", { e ->
            if (e !is org.w3c.dom.events.MouseEvent) return@addEventListener

            mouseIsPressed = false
            events.add(createMouseEvent(e, MouseEventType.MOUSE_RELEASED) to UIEventPhase.TARGET)
        })
        canvas.addEventListener("mousedown", { e ->
            if (e !is org.w3c.dom.events.MouseEvent) return@addEventListener

            mouseIsPressed = true
            events.add(createMouseEvent(e, MouseEventType.MOUSE_PRESSED) to UIEventPhase.TARGET)
        })

        canvas.addEventListener("mousemove", { e ->
            if (e !is org.w3c.dom.events.MouseEvent) return@addEventListener

            val type = if (mouseIsPressed) MouseEventType.MOUSE_DRAGGED else MouseEventType.MOUSE_MOVED
            events.add(createMouseEvent(e, type) to UIEventPhase.TARGET)
        })

        canvas.addEventListener("wheel", { e ->
            if (e !is org.w3c.dom.events.WheelEvent) return@addEventListener

            when {
                e.deltaY > 0.0 -> {
                    events.add(createMouseEvent(e, MouseEventType.MOUSE_WHEEL_ROTATED_DOWN) to UIEventPhase.TARGET)
                }
                e.deltaY < 0.0 -> {
                    events.add(createMouseEvent(e, MouseEventType.MOUSE_WHEEL_ROTATED_UP) to UIEventPhase.TARGET)
                }

            }
        })
    }

    fun drainEvents(): Iterable<Pair<MouseEvent, UIEventPhase>> {
        return events.toList().also {
            events.clear()
        }
    }

    private fun createMouseEvent(e: org.w3c.dom.events.MouseEvent, type: MouseEventType): MouseEvent {
        val position = Position.create(
                x = 0.coerceAtLeast(e.clientX.div(fontWidth)),
                y = 0.coerceAtLeast(e.clientY.div(fontHeight)))
        return MouseEvent(type, e.button.toInt(), position)
    }

}