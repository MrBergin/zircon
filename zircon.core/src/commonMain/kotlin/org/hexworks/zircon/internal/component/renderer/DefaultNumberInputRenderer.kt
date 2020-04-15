package org.hexworks.zircon.internal.component.renderer

import org.hexworks.zircon.api.component.renderer.ComponentRenderContext
import org.hexworks.zircon.api.component.renderer.ComponentRenderer
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.internal.component.impl.BaseNumberInput

class DefaultNumberInputRenderer : ComponentRenderer<BaseNumberInput> {

    override fun render(tileGraphics: TileGraphics, context: ComponentRenderContext<BaseNumberInput>) {
        val component = context.component
        val style = context.currentStyle
        tileGraphics.applyStyle(style)
        val tileTemplate = Tile.createCharacterTile(' ', style)
        tileGraphics.size.fetchPositions().forEach { pos ->
            component.textBuffer().getCharAt(pos).fold(
                    whenEmpty = {
                        tileGraphics.draw(tileTemplate, pos)
                    },
                    whenPresent = { char ->
                        tileGraphics.draw(tileTemplate.withCharacter(char), pos)
                    })
        }
    }
}
