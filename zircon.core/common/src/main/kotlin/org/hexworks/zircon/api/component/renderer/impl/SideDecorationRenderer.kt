package org.hexworks.zircon.api.component.renderer.impl

import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.component.renderer.ComponentDecorationRenderContext
import org.hexworks.zircon.api.component.renderer.ComponentDecorationRenderer
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.extensions.toCharacterTile
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.api.graphics.impl.SubTileGraphics

data class SideDecorationRenderer(
        private val leftSideCharacter: Char = '[',
        private val rightSideCharacter: Char = ']') : ComponentDecorationRenderer {

    override val offset = Position.create(1, 0)

    override val occupiedSize = Size.create(2, 0)

    override fun render(tileGraphics: TileGraphics, context: ComponentDecorationRenderContext) {
        0.until(tileGraphics.height).forEach { idx ->
            tileGraphics.draw(leftSideCharacter.toCharacterTile(), Positions.create(0, idx))
            tileGraphics.draw(rightSideCharacter.toCharacterTile(), Positions.create(tileGraphics.size.width - 1, idx))
        }
        tileGraphics.applyStyle(context.component.componentStyleSet.currentStyle())
    }
}
