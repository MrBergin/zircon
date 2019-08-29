package org.hexworks.zircon.internal.component

import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Container
import org.hexworks.zircon.api.data.LayerState
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.internal.behavior.Focusable
import org.hexworks.zircon.internal.uievent.ComponentEventAdapter
import org.hexworks.zircon.internal.uievent.KeyboardEventAdapter
import org.hexworks.zircon.internal.uievent.MouseEventAdapter
import org.hexworks.zircon.internal.uievent.UIEventProcessor
import org.hexworks.zircon.internal.event.ZirconEvent.ComponentMoved

/**
 * A [InternalComponent] is a specialization of the [Component] interface which adds
 * functionality which will be used by Zircon internally. This makes it possible to have
 * a clean API for [Component]s but enables Zircon and the developers of custom [Component]s
 * to interact with them in a more meaningful manner.
 */
interface InternalComponent : Component, ComponentEventAdapter, Focusable,
        KeyboardEventAdapter, MouseEventAdapter, UIEventProcessor {

    /**
     * The state of this [InternalComponent].
     */
    val state: LayerState

    /**
     * The immediate child [Component]s of this [Container].
     */
    val children: Iterable<InternalComponent>

    /**
     * All descendant [Component]s of this [Container].
     */
    val descendants: Iterable<InternalComponent>

    /**
     * The [org.hexworks.zircon.api.graphics.TileGraphics] which this
     * component uses for drawing.
     */
    val graphics: TileGraphics

    override fun isAttached(): Boolean = fetchParent().isPresent

    /**
     * Moves this [InternalComponent] to the given [position].
     * If [signalComponentChange] is `true` this funciton will send
     * a [ComponentMoved] event.
     */
    fun moveTo(position: Position, signalComponentChange: Boolean)

    /**
     * Attaches this [Component] to the given parent [Container].
     * Note that if this component is already attached to a [Container]
     * it will be removed from that one.
     */
    fun attachTo(parent: InternalContainer)

    /**
     * Returns the innermost [InternalComponent] for a given [Position].
     * This means that if you call this method on a [Container] and it
     * contains a [InternalComponent] which intersects with `position` the
     * component will be returned instead of the container itself.
     * If no [InternalComponent] intersects with the given `position` an
     * empty [Maybe] is returned.
     */
    fun fetchComponentByPosition(absolutePosition: Position): Maybe<out InternalComponent>

    /**
     * Returns the parent of this [Component] (if any).
     */
    fun fetchParent(): Maybe<InternalContainer>

    /**
     * Recursively traverses the parents of this [InternalComponent]
     * until the root is reached and returns them.
     */
    fun calculatePathFromRoot(): Iterable<InternalComponent>

    /**
     * Renders this component to the underlying [TileGraphics].
     */
    fun render()

}
