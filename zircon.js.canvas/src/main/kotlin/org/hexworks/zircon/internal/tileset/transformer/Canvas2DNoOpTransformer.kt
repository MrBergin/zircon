package org.hexworks.zircon.internal.tileset.transformer

import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.tileset.TextureTransformer
import org.hexworks.zircon.api.tileset.TileTexture
import org.w3c.dom.HTMLCanvasElement


class Canvas2DNoOpTransformer : TextureTransformer<HTMLCanvasElement> {

    override fun transform(texture: TileTexture<HTMLCanvasElement>, tile: Tile) = texture
}