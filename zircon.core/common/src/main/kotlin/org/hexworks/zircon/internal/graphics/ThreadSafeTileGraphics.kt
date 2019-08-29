package org.hexworks.zircon.internal.graphics

import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.data.DrawSurfaceState
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.api.graphics.base.BaseTileGraphics
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.internal.data.DefaultDrawSurfaceState
import org.hexworks.zircon.platform.factory.PersistentMapFactory
import kotlin.jvm.Synchronized

/**
 * This is a read-safe [TileGraphics] implementation. Read safety means
 * that all read operations ([getTileAt], [state], etc) are consistent
 * even if concurrent write operations are being performed. Use this implementation
 * if you own't use multiple threads when writing to this object.
 */
class ThreadSafeTileGraphics(
        initialSize: Size,
        initialTileset: TilesetResource,
        initialTiles: Map<Position, Tile> = mapOf())
    : BaseTileGraphics(initialSize = initialSize, initialTileset = initialTileset) {

    override var tiles = PersistentMapFactory.create<Position, Tile>()
            .putAll(initialTiles)
        private set

    override var tileset: TilesetResource = initialTileset
        @Synchronized
        set(value) {
            value.checkCompatibilityWith(field)
            field = value
            currentState = currentState.copy(tileset = value)
        }

    override val state: DrawSurfaceState
        get() = currentState

    private var currentState = DefaultDrawSurfaceState(
            size = initialSize,
            tileset = initialTileset,
            tiles = initialTiles)

    @Synchronized
    override fun draw(tilesToDraw: Map<Position, Tile>, drawAt: Position, drawArea: Size) {
        tiles = tiles.putAll(tilesToDraw.asSequence()
                .filter { drawArea.containsPosition(it.key) && size.containsPosition(it.key + drawAt) }
                .map { it.key + drawAt to it.value }
                .toMap())
        currentState = currentState.copy(tiles = tiles)
    }

    @Synchronized
    override fun draw(tileToDraw: Tile, drawAt: Position) {
        if (size.containsPosition(drawAt)) {
            tiles = tiles.put(drawAt, tileToDraw)
            currentState = currentState.copy(tiles = tiles)
        }
    }

    @Synchronized
    override fun clear() {
        tiles = tiles.clear()
        currentState = currentState.copy(tiles = tiles)
    }

    @Synchronized
    override fun fill(filler: Tile) {
        val (currentTiles, _, currentSize) = currentState
        tiles = tiles.putAll(currentSize.fetchPositions()
                .minus(currentTiles.filterValues { it != Tiles.empty() }.keys)
                .map { it to filler }.toMap())
        currentState = currentState.copy(tiles = tiles)
    }

    @Synchronized
    override fun transformTileAt(position: Position, tileTransformer: (Tile) -> Tile) {
        tiles[position]?.let { tile ->
            tiles = tiles.put(position, tileTransformer(tile))
            currentState = currentState.copy(tiles = tiles)
        }
    }
}
