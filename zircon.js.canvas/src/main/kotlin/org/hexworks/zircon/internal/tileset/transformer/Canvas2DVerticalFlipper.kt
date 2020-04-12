package org.hexworks.zircon.internal.tileset.transformer

import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.tileset.TextureTransformer
import org.hexworks.zircon.api.tileset.TileTexture
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

fun HTMLCanvasElement.context2D() = getContext("2d") as CanvasRenderingContext2D

class Canvas2DVerticalFlipper : TextureTransformer<HTMLCanvasElement> {

    override fun transform(texture: TileTexture<HTMLCanvasElement>, tile: Tile): TileTexture<HTMLCanvasElement> {
        return texture.also {
            it.texture.context2D().translate(1.0, -1.0)
        }
    }
}