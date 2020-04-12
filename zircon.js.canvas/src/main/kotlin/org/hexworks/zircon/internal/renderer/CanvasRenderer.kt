package org.hexworks.zircon.internal.renderer

import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.api.application.AppConfig
import org.hexworks.zircon.api.behavior.TilesetHolder
import org.hexworks.zircon.api.data.CharacterTile
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.modifier.TileTransformModifier
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.api.tileset.Tileset
import org.hexworks.zircon.internal.application.KeyboardEventListener
import org.hexworks.zircon.internal.application.MouseEventListener
import org.hexworks.zircon.internal.grid.InternalTileGrid
import org.hexworks.zircon.internal.tileset.CanvasTilesetLoader
import org.hexworks.zircon.internal.tileset.transformer.context2D
import org.hexworks.zircon.platform.util.SystemUtils
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document

class CanvasRenderer(
        private val config: AppConfig,
        private val tileGrid: InternalTileGrid,
        private val canvas: HTMLCanvasElement
) : Renderer {
    private val keyboardEventListener = KeyboardEventListener(canvas)
    private val mouseEventListener = MouseEventListener(canvas, tileGrid.tileset.width, tileGrid.tileset.height)
    private var blinkOn = true
    private var lastRender: Long = SystemUtils.getCurrentTimeMs()
    private var lastBlink: Long = lastRender
    private val canvasTilesetLoader = CanvasTilesetLoader()

    override fun create() {
    }

    private val buffer = (document.createElement("canvas") as HTMLCanvasElement).apply {
        width = tileGrid.widthInPixels
        height = tileGrid.heightInPixels
    }
    private val ctx = buffer.context2D()
    private val realCtx = canvas.context2D()

    override suspend fun render() {
        val now = SystemUtils.getCurrentTimeMs()

        keyboardEventListener.drainEvents().forEach { (event, phase) ->
            tileGrid.process(event, phase)
        }

        mouseEventListener.drainEvents().forEach { (event, phase) ->
            tileGrid.process(event, phase)
        }

        tileGrid.updateAnimations(now, tileGrid)

        (buffer.getContext("2d") as CanvasRenderingContext2D).apply {
            val oldFillStyle = fillStyle
            fillStyle = "black"
            fillRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
            fillStyle = oldFillStyle
        }

        handleBlink(now)

        val tilesToRender = linkedMapOf<Position, MutableList<Pair<Tile, TilesetResource>>>()

        val layerStates = tileGrid.layerStates

        layerStates.forEach { state ->
            if (state.isHidden.not()) {
                state.tiles.forEach { (tilePos, tile) ->
                    var finalTile = tile
                    finalTile.modifiers.filterIsInstance<TileTransformModifier<CharacterTile>>().forEach { modifier ->
                        if (modifier.canTransform(finalTile)) {
                            (finalTile as? CharacterTile)?.let {
                                finalTile = modifier.transform(it)
                            }
                        }
                    }
                    val finalPos = tilePos + state.position
                    tilesToRender.getOrPut(finalPos) { mutableListOf() }
                    if (finalTile.isOpaque) {
                        tilesToRender[finalPos] = mutableListOf(finalTile to state.tileset)
                    } else {
                        tilesToRender[finalPos]?.add(finalTile to state.tileset)
                    }
                }
            }
        }

        tilesToRender.map { (pos, tiles) ->
            tiles.forEach { (tile, tileset) ->
                renderTile(ctx, pos, tile, canvasTilesetLoader.loadTilesetFrom(tileset))
            }
        }

        realCtx.drawImage(buffer, 0.0, 0.0)
    }

    private fun handleBlink(now: Long) {
        if (now > lastBlink + config.blinkLengthInMilliSeconds) {
            blinkOn = blinkOn.not()
            lastBlink = now
        }
    }

    private suspend fun renderTile(graphics: CanvasRenderingContext2D,
                                   position: Position,
                                   tile: Tile,
                                   tileset: Tileset<CanvasRenderingContext2D>) {
        if (tile.isNotEmpty) {
            val actualTile = if (tile.isBlinking && blinkOn) {
                tile.withBackgroundColor(tile.foregroundColor)
                        .withForegroundColor(tile.backgroundColor)
            } else {
                tile
            }
            if (actualTile is TilesetHolder) {
                canvasTilesetLoader.loadTilesetFrom(actualTile.tileset)
            } else {
                tileset
            }.drawTile(tile = actualTile,
                    surface = graphics,
                    position = position)
        }
    }

    override fun close() {
    }

    override val isClosed: ObservableValue<Boolean>
        get() = false.toProperty()

}