package org.hexworks.zircon.internal.component.renderer

import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.builder.graphics.CharacterTileStringBuilder
import org.hexworks.zircon.api.component.renderer.ComponentRenderContext
import org.hexworks.zircon.api.component.renderer.ComponentRenderer
import org.hexworks.zircon.api.graphics.TextWrap
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.internal.component.impl.DefaultParagraph

class DefaultParagraphRenderer : ComponentRenderer<DefaultParagraph> {

    override fun render(tileGraphics: TileGraphics, context: ComponentRenderContext<DefaultParagraph>) {
        val style = context.componentStyle.currentStyle()
        val text = context.component.text
        tileGraphics.clear()
        tileGraphics.draw(CharacterTileStringBuilder.newBuilder()
                .withText(text)
                .withSize(Sizes.create(text.length, 1))
                .withTextWrap(TextWrap.WORD_WRAP)
                .build())
        tileGraphics.applyStyle(style)
    }
}
