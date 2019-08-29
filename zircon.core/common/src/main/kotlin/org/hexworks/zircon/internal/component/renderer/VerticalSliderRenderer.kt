package org.hexworks.zircon.internal.component.renderer

import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.component.Slider
import org.hexworks.zircon.api.component.data.ComponentState
import org.hexworks.zircon.api.component.renderer.ComponentRenderContext
import org.hexworks.zircon.api.component.renderer.ComponentRenderer
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.Symbols
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.api.graphics.impl.SubTileGraphics

class VerticalSliderRenderer : ComponentRenderer<Slider> {

    override fun render(tileGraphics: TileGraphics, context: ComponentRenderContext<Slider>) {
        val style = context.componentStyle.currentStyle()
        tileGraphics.applyStyle(style)

        val defaultStyleSet = context.componentStyle.fetchStyleFor(ComponentState.DEFAULT)
        val invertedDefaultStyleSet = defaultStyleSet
                .withBackgroundColor(defaultStyleSet.foregroundColor)
                .withForegroundColor(defaultStyleSet.backgroundColor)
        val disabledStyleSet = context.componentStyle.fetchStyleFor(ComponentState.DISABLED)

        val cursorPosition = context.component.currentStep
        val barWidth = context.component.numberOfSteps

        (0..barWidth).forEach { idx ->
            when {
                idx == cursorPosition -> tileGraphics.draw(Tile.createCharacterTile(Symbols.DOUBLE_LINE_HORIZONTAL, style), Positions.create(0, idx))
                idx < cursorPosition -> tileGraphics.draw(Tile.createCharacterTile(' ', invertedDefaultStyleSet), Positions.create(0, idx))
                else -> tileGraphics.draw(Tile.createCharacterTile(' ', disabledStyleSet), Positions.create(0, idx))
            }
        }
    }
}
