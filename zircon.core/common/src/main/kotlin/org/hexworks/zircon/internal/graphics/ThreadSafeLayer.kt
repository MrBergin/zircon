package org.hexworks.zircon.internal.graphics

import org.hexworks.cobalt.Identifier
import org.hexworks.cobalt.databinding.api.createPropertyFrom
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.factory.IdentifierFactory
import org.hexworks.zircon.api.behavior.Clearable
import org.hexworks.zircon.api.behavior.Movable
import org.hexworks.zircon.api.builder.graphics.TileGraphicsBuilder
import org.hexworks.zircon.api.data.LayerState
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Rect
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.extensions.toTileGraphics
import org.hexworks.zircon.api.graphics.DrawSurface
import org.hexworks.zircon.api.graphics.Layer
import org.hexworks.zircon.api.graphics.StyleSet
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.api.graphics.impl.SubTileGraphics
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.internal.Zircon
import org.hexworks.zircon.internal.behavior.impl.DefaultMovable
import org.hexworks.zircon.internal.data.DefaultLayerState
import org.hexworks.zircon.internal.event.ZirconEvent
import org.hexworks.zircon.internal.event.ZirconScope
import kotlin.jvm.Synchronized

open class ThreadSafeLayer(
        initialPosition: Position,
        initialContents: TileGraphics,
        private val movable: Movable = DefaultMovable(
                position = initialPosition,
                size = initialContents.size)) : Clearable, DrawSurface, Layer, Movable {

    final override val tiles: Map<Position, Tile>
        get() = currentState.tiles

    final override val size: Size
        get() = currentState.size

    final override val width: Int
        get() = currentState.size.width

    final override val height: Int
        get() = currentState.size.height

    final override val rect: Rect
        get() = movable.rect

    final override val state: LayerState
        get() = currentState

    final override val id: Identifier = IdentifierFactory.randomIdentifier()

    final override val hiddenProperty = createPropertyFrom(false)

    final override var isHidden: Boolean by hiddenProperty.asDelegate()

    init {
        hiddenProperty.onChange {
            replaceState(currentState.copy(isHidden = it.newValue))
        }
    }

    final override var tileset: TilesetResource
        get() = currentState.tileset
        @Synchronized
        set(value) {
            backend.tileset = value
            replaceState(currentState.copy(tileset = value))
        }

    private var currentState: DefaultLayerState = DefaultLayerState(
            tiles = initialContents.tiles,
            tileset = initialContents.tileset,
            size = initialContents.size,
            position = initialPosition,
            isHidden = false,
            id = id)

    private val backend = TileGraphicsBuilder.newBuilder()
            .withSize(initialContents.size)
            .withTiles(initialContents.tiles)
            .withTileset(initialContents.tileset)
            .buildThreadSafeTileGraphics()

    final override fun getAbsoluteTileAt(position: Position): Maybe<Tile> {
        return backend.getTileAt(position - this.position)
    }

    @Synchronized
    final override fun setAbsoluteTileAt(position: Position, tile: Tile) {
        backend.draw(tile, position - this.position)
        replaceState(currentState.copy(tiles = backend.tiles))
    }

    final override fun createCopy(): Layer {
        val (currentTiles, currentTileset, currentSize, _, currentPosition, currentlyHidden) = currentState
        return ThreadSafeLayer(
                initialPosition = currentPosition,
                initialContents = currentTiles.toTileGraphics(currentSize, currentTileset)).apply {
            isHidden = currentlyHidden
        }
    }

    final override fun toSubTileGraphics(rect: Rect) = SubTileGraphics(rect, this)

    @Synchronized
    override fun moveTo(position: Position) {
        movable.moveTo(position)
        replaceState(currentState.copy(position = position))
    }

    @Synchronized
    final override fun draw(tileMap: Map<Position, Tile>, drawPosition: Position, drawArea: Size) {
        backend.draw(tileMap, drawPosition, drawArea)
        replaceState(currentState.copy(tiles = backend.tiles))
    }


    @Synchronized
    final override fun draw(tile: Tile, drawPosition: Position) {
        backend.draw(tile, drawPosition)
        replaceState(currentState.copy(tiles = backend.tiles))
    }

    @Synchronized
    final override fun transformTileAt(position: Position, tileTransformer: (Tile) -> Tile) {
        backend.transformTileAt(position, tileTransformer)
        replaceState(currentState.copy(tiles = backend.tiles))
    }

    @Synchronized
    final override fun fill(filler: Tile) {
        backend.fill(filler)
        replaceState(currentState.copy(tiles = backend.tiles))
    }

    @Synchronized
    final override fun transform(transformer: (Tile) -> Tile) {
        backend.transform(transformer)
        replaceState(currentState.copy(tiles = backend.tiles))
    }

    final override fun applyStyle(styleSet: StyleSet) {
        backend.applyStyle(styleSet)
        replaceState(currentState.copy(tiles = backend.tiles))
    }

    @Synchronized
    final override fun clear() {
        backend.clear()
        replaceState(currentState.copy(tiles = backend.tiles))
    }

    /**
     * Refreshes the state of this [ThreadSafeLayer] by replacing
     * the old state with a completely reconstructed [LayerState].
     */
    @Synchronized
    protected fun refreshState() {
        currentState = DefaultLayerState(
                tiles = tiles,
                tileset = tileset,
                size = size,
                id = id,
                position = position,
                isHidden = isHidden)
        Zircon.eventBus.publish(
                event = ZirconEvent.LayerChanged(state),
                eventScope = ZirconScope)
    }

    @Synchronized
    private fun replaceState(newState: DefaultLayerState) {
        currentState = newState
        Zircon.eventBus.publish(
                event = ZirconEvent.LayerChanged(state),
                eventScope = ZirconScope)
    }
}
