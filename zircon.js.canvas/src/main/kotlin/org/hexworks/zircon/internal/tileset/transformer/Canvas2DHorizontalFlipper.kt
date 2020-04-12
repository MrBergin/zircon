package org.hexworks.zircon.internal.tileset.transformer

import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.tileset.TextureTransformer
import org.hexworks.zircon.api.tileset.TileTexture
import org.hexworks.zircon.internal.tileset.impl.DefaultTileTexture
import org.w3c.dom.HTMLCanvasElement

class Canvas2DHorizontalFlipper : TextureTransformer<HTMLCanvasElement> {

    override fun transform(texture: TileTexture<HTMLCanvasElement>, tile: Tile): TileTexture<HTMLCanvasElement> {
        return texture.also {
            it.texture.context2D().translate(-1.0, 1.0)
        }
    }
}