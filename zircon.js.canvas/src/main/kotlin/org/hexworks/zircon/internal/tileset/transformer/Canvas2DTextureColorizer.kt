package org.hexworks.zircon.internal.tileset.transformer

import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.tileset.TextureTransformer
import org.hexworks.zircon.api.tileset.TileTexture
import org.w3c.dom.HTMLCanvasElement

class CanvasDTextureColorizer : TextureTransformer<HTMLCanvasElement> {

    override fun transform(texture: TileTexture<HTMLCanvasElement>, tile: Tile): TileTexture<HTMLCanvasElement> {
        val r = tile.foregroundColor.red.toFloat() / 255
        val g = tile.foregroundColor.green.toFloat() / 255
        val b = tile.foregroundColor.blue.toFloat() / 255

        val backend = texture.texture
        val ctx = backend.context2D()

        (0 until backend.width).forEach { x ->
            (0 until backend.height).forEach { y ->
                val pixel = ctx.getImageData(x.toDouble(), y.toDouble(), 1.0, 1.0)
                val data: dynamic = pixel.data
                var rx = data[0]
                var gx = data[1]
                var bx = data[2]
                val ax = data[3]
                rx = (rx * r)
                gx = (gx * g)
                bx = (bx * b)
                if (ax < 50) {
                    data[0] = tile.backgroundColor.red
                    data[1] = tile.backgroundColor.green
                    data[2] = tile.backgroundColor.blue
                    data[3] = 255
                    ctx.putImageData(pixel, x.toDouble(), y.toDouble())
                } else {
                    data[0] = rx
                    data[1] = gx
                    data[2] = bx
                    data[3] = ax
                    ctx.putImageData(pixel, x.toDouble(), y.toDouble())
                }
            }
        }
        return texture
    }

}
