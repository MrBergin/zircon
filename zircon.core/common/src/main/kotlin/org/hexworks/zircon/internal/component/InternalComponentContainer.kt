package org.hexworks.zircon.internal.component

import org.hexworks.zircon.internal.behavior.LayerStateHolder
import org.hexworks.zircon.api.component.ColorTheme
import org.hexworks.zircon.api.component.ComponentContainer
import org.hexworks.zircon.api.component.ComponentStyleSet
import org.hexworks.zircon.internal.uievent.UIEventDispatcher
import org.hexworks.zircon.api.uievent.UIEvent

/**
 * Internal API for a [ComponentContainer] which also augments it with
 * handling [layerStates] and [UIEvent]s.
 */
interface InternalComponentContainer : ComponentContainer, LayerStateHolder, UIEventDispatcher {

    /**
     * Tells whether this [InternalComponentContainer] is active or not.
     */
    fun isActive(): Boolean

    /**
     * Activates this [InternalComponentContainer]. It will (re) start listening to
     * its related events.
     */
    fun activate()

    /**
     * Deactivates this [InternalComponentContainer]. It will no longer act on
     * container-related events.
     */
    fun deactivate()

    /**
     * Applies a [ColorTheme] to this component and recursively to all its children (if any).
     * @return the [ComponentStyleSet] which the [ColorTheme] was converted to.
     */
    fun applyColorTheme(colorTheme: ColorTheme): ComponentStyleSet

}
