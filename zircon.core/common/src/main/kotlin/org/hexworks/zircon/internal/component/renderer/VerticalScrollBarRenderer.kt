package org.hexworks.zircon.internal.component.renderer

import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.component.ScrollBar
import org.hexworks.zircon.api.component.data.ComponentState
import org.hexworks.zircon.api.component.renderer.ComponentRenderContext
import org.hexworks.zircon.api.component.renderer.ComponentRenderer
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.api.graphics.impl.SubTileGraphics

class VerticalScrollBarRenderer : ComponentRenderer<ScrollBar> {

    override fun render(tileGraphics: TileGraphics, context: ComponentRenderContext<ScrollBar>) {
        val style = context.componentStyle.currentStyle()
        tileGraphics.applyStyle(style)

        val defaultStyleSet = context.componentStyle.fetchStyleFor(ComponentState.DEFAULT)
        val invertedDefaultStyleSet = defaultStyleSet
                .withBackgroundColor(defaultStyleSet.foregroundColor)
                .withForegroundColor(defaultStyleSet.backgroundColor)
        val disabledStyleSet = context.componentStyle.fetchStyleFor(ComponentState.DISABLED)

        val lowBarPosition = context.component.currentStep
        val highBarPosition = lowBarPosition + context.component.barSizeInSteps - 1
        val totalScrollBarHeight = context.component.contentSize.height

        (0..totalScrollBarHeight).forEach { idx ->
            when {
                idx < lowBarPosition -> tileGraphics.draw(Tile.createCharacterTile(' ', disabledStyleSet), Positions.create(0, idx))
                idx > highBarPosition  -> tileGraphics.draw(Tile.createCharacterTile(' ', disabledStyleSet), Positions.create(0, idx))
                else -> tileGraphics.draw(Tile.createCharacterTile(' ', invertedDefaultStyleSet), Positions.create(0, idx))
            }
        }
    }
}
