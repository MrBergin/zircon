package org.hexworks.zircon.internal.shape

import org.hexworks.zircon.api.builder.graphics.TileGraphicsBuilder
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.api.shape.Shape
import kotlin.math.max

class DefaultShape(override val positions: Set<Position> = setOf())
    : Shape, Collection<Position> by positions {

    override fun toTileGraphics(tile: Tile, tileset: TilesetResource): TileGraphics {
        val offsetPositions = offsetToDefaultPosition()
        var maxCol = Int.MIN_VALUE
        var maxRow = Int.MIN_VALUE
        offsetPositions.forEach { (col, row) ->
            maxCol = max(maxCol, col)
            maxRow = max(maxRow, row)
        }
        val result = TileGraphicsBuilder.newBuilder()
                .withSize(Size.create(maxCol + 1, maxRow + 1))
                .withTileset(tileset)
                .build()
        offsetPositions.forEach {
            result.draw(tile, it)
        }
        return result
    }

    override fun offsetToDefaultPosition(): Shape {
        require(positions.isNotEmpty()) {
            "You can't transform a Shape with zero points!"
        }
        val offset = Position.create(
                x = positions.minBy { it.x }!!.x,
                y = positions.minBy { it.y }!!.y
        )
        return DefaultShape(positions.map { it - offset }
                .toSet())
    }
}
