package org.hexworks.zircon.internal.tileset.transformer

import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.tileset.TextureTransformer
import org.hexworks.zircon.api.tileset.TileTexture
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

fun TileColor.toCanvasString() = "rbga($red, $green, $blue, $alpha)"

class Canvas2DUnderlineTransformer : TextureTransformer<HTMLCanvasElement> {

    override fun transform(texture: TileTexture<HTMLCanvasElement>, tile: Tile): TileTexture<HTMLCanvasElement> {
        return texture.also {
            it.texture.let { txt ->
                (txt.getContext("2d") as CanvasRenderingContext2D).apply {
                    fillStyle = tile.foregroundColor.toCanvasString()
                    fillRect(0.0, txt.height - 2.0, txt.width.toDouble(), 2.0)
                }
            }
        }
    }
}
