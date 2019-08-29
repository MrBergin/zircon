package org.hexworks.zircon.internal.behavior

import org.hexworks.zircon.api.data.LayerState
import org.hexworks.zircon.api.graphics.Layer
import org.hexworks.zircon.internal.util.PersistentList

/**
 * Represents an object which contains [Layer]s and provides a way
 * for accessing their states.
 */
interface LayerStateHolder {

    /**
     * Holds a snapshot of the states of all [Layer]s in this [LayerStateHolder].
     */
    val layerStates: PersistentList<LayerState>
}
