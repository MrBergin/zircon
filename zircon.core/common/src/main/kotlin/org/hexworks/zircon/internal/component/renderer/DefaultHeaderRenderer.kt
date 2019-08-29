package org.hexworks.zircon.internal.component.renderer

import org.hexworks.zircon.api.component.renderer.ComponentRenderContext
import org.hexworks.zircon.api.component.renderer.ComponentRenderer
import org.hexworks.zircon.api.extensions.toCharacterTileString
import org.hexworks.zircon.api.graphics.TextWrap
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.internal.component.impl.DefaultHeader

class DefaultHeaderRenderer : ComponentRenderer<DefaultHeader> {

    override fun render(tileGraphics: TileGraphics, context: ComponentRenderContext<DefaultHeader>) {
        val style = context.componentStyle.currentStyle()
        tileGraphics.clear()
        tileGraphics.draw(context.component.text.toCharacterTileString(
                textWrap = TextWrap.WORD_WRAP))
        tileGraphics.applyStyle(style)
    }
}
