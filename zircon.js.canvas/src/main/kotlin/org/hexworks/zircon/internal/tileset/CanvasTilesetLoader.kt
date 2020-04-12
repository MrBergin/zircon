package org.hexworks.zircon.internal.tileset

import org.hexworks.cobalt.core.api.UUID
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.zircon.api.behavior.Closeable
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.api.tileset.Tileset
import org.hexworks.zircon.api.tileset.TilesetLoader
import org.hexworks.zircon.internal.resource.TileType
import org.hexworks.zircon.internal.resource.TilesetType
import org.w3c.dom.CanvasRenderingContext2D

class CanvasTilesetLoader : TilesetLoader<CanvasRenderingContext2D>, Closeable {

    override val isClosed = false.toProperty()

    private val tilesetCache = mutableMapOf<UUID, Tileset<CanvasRenderingContext2D>>()

    override fun loadTilesetFrom(resource: TilesetResource): Tileset<CanvasRenderingContext2D> {
        return tilesetCache.getOrPut(resource.id) {
            LOADERS[resource.getLoaderKey()]?.invoke(resource)
                    ?: throw IllegalArgumentException("Unknown tile type '${resource.tileType}'.§")
        }
    }

    override fun close() {
        isClosed.value = true
        tilesetCache.clear()
    }

    companion object {

        fun TilesetResource.getLoaderKey() = "${this.tileType.name}-${this.tilesetType.name}"

        private val LOADERS: Map<String, (TilesetResource) -> Tileset<CanvasRenderingContext2D>> = mapOf(
                "${TileType.CHARACTER_TILE}-${TilesetType.CP437_TILESET}" to { resource: TilesetResource ->
                    Canvas2DCP437Tileset(
                            resource = resource)
                })
    }
}
