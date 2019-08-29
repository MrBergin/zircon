package org.hexworks.zircon.internal.component.renderer

import org.hexworks.zircon.api.CharacterTileStrings
import org.hexworks.zircon.api.component.renderer.ComponentRenderContext
import org.hexworks.zircon.api.component.renderer.ComponentRenderer
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.internal.component.impl.DefaultLabel

class DefaultLabelRenderer : ComponentRenderer<DefaultLabel> {

    override fun render(tileGraphics: TileGraphics, context: ComponentRenderContext<DefaultLabel>) {
        val style = context.componentStyle.currentStyle()
        val text = context.component.text
        tileGraphics.clear()
        tileGraphics.draw(CharacterTileStrings
                .newBuilder()
                .withText(text)
                .build())
        tileGraphics.applyStyle(style)
    }
}
