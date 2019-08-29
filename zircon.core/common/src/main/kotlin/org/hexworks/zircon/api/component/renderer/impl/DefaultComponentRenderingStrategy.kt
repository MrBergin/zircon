package org.hexworks.zircon.api.component.renderer.impl

import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.renderer.ComponentDecorationRenderContext
import org.hexworks.zircon.api.component.renderer.ComponentDecorationRenderer
import org.hexworks.zircon.api.component.renderer.ComponentPostProcessor
import org.hexworks.zircon.api.component.renderer.ComponentPostProcessorContext
import org.hexworks.zircon.api.component.renderer.ComponentRenderContext
import org.hexworks.zircon.api.component.renderer.ComponentRenderer
import org.hexworks.zircon.api.component.renderer.ComponentRenderingStrategy
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Rect
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.internal.component.InternalComponent

class DefaultComponentRenderingStrategy<T : Component>(
        override val componentRenderer: ComponentRenderer<in T>,
        override val decorationRenderers: List<ComponentDecorationRenderer> = listOf(),
        override val componentPostProcessors: List<ComponentPostProcessor<T>> = listOf()) : ComponentRenderingStrategy<T> {

    override fun render(component: T, graphics: TileGraphics) {

        if (component.isHidden.not()) {
            var currentOffset = Position.defaultPosition()
            var currentSize = graphics.size

            val componentArea = graphics.toSubTileGraphics(Rect.create(
                    position = decorationRenderers
                            .map { it.offset }.fold(Position.zero(), Position::plus),
                    size = graphics.size - decorationRenderers
                            .map { it.occupiedSize }.fold(Size.zero(), Size::plus)))

            componentRenderer.render(
                    tileGraphics = componentArea,
                    context = ComponentRenderContext(component))

            decorationRenderers.forEach { renderer ->
                val bounds = Rect.create(currentOffset, currentSize)
                renderer.render(graphics.toSubTileGraphics(bounds), ComponentDecorationRenderContext(component))
                currentOffset += renderer.offset
                currentSize -= renderer.occupiedSize
            }

            componentPostProcessors.forEach { renderer ->
                renderer.render(componentArea, ComponentPostProcessorContext(component))
            }
        }
    }

    override fun calculateContentPosition(): Position = decorationRenderers.asSequence().map {
        it.offset
    }.fold(Position.defaultPosition(), Position::plus)

    override fun calculateContentSize(componentSize: Size): Size = componentSize -
            decorationRenderers.asSequence().map {
                it.occupiedSize
            }.fold(Size.zero(), Size::plus)

}
