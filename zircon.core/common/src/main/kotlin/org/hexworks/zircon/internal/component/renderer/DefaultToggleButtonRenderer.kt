package org.hexworks.zircon.internal.component.renderer

import org.hexworks.zircon.api.CharacterTileStrings
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.renderer.ComponentRenderContext
import org.hexworks.zircon.api.component.renderer.ComponentRenderer
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.api.graphics.impl.SubTileGraphics
import org.hexworks.zircon.internal.component.impl.DefaultButton
import org.hexworks.zircon.internal.component.impl.DefaultToggleButton

class DefaultToggleButtonRenderer : ComponentRenderer<DefaultToggleButton> {

    override fun render(tileGraphics: TileGraphics, context: ComponentRenderContext<DefaultToggleButton>) {
        val style = context.componentStyle.currentStyle()
        val text = context.component.text
        tileGraphics.draw(CharacterTileStrings
                .newBuilder()
                .withSize(Sizes.create(text.length, 1))
                .withText(text).build())
        tileGraphics.applyStyle(style)
    }

}
