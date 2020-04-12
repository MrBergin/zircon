package org.hexworks.zircon.internal.tileset.transformer

import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.tileset.TextureTransformer
import org.hexworks.zircon.api.tileset.TileTexture
import org.hexworks.zircon.internal.tileset.impl.DefaultTileTexture
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document

class Canvas2DTextureCloner : TextureTransformer<HTMLCanvasElement> {

    override fun transform(texture: TileTexture<HTMLCanvasElement>, tile: Tile): TileTexture<HTMLCanvasElement> {
        val oldCanvas = texture.texture
        val newCanvas = document.createElement("canvas") as HTMLCanvasElement
        val context = newCanvas.context2D()
        newCanvas.width = oldCanvas.width
        newCanvas.height = oldCanvas.height

        context.drawImage(oldCanvas, 0.0, 0.0)

        return DefaultTileTexture(
                width = oldCanvas.width,
                height = oldCanvas.height,
                texture = newCanvas)
    }
}
