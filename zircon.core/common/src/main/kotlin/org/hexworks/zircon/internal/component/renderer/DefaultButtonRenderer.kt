package org.hexworks.zircon.internal.component.renderer

import org.hexworks.zircon.api.component.renderer.ComponentRenderContext
import org.hexworks.zircon.api.component.renderer.ComponentRenderer
import org.hexworks.zircon.api.extensions.toCharacterTileString
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.internal.component.impl.DefaultButton

class DefaultButtonRenderer : ComponentRenderer<DefaultButton> {

    override fun render(tileGraphics: TileGraphics, context: ComponentRenderContext<DefaultButton>) {
        val style = context.componentStyle.currentStyle()
        tileGraphics.clear()
        tileGraphics.draw(context.component.text.toCharacterTileString())
        tileGraphics.applyStyle(style)
    }

}
