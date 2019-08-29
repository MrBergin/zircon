package org.hexworks.zircon.internal.behavior.impl

import org.hexworks.cobalt.events.api.subscribe
import org.hexworks.zircon.api.data.LayerState
import org.hexworks.zircon.internal.Zircon
import org.hexworks.zircon.internal.behavior.InternalLayerable
import org.hexworks.zircon.internal.component.InternalComponentContainer
import org.hexworks.zircon.internal.event.ZirconEvent.ComponentAdded
import org.hexworks.zircon.internal.event.ZirconEvent.ComponentMoved
import org.hexworks.zircon.internal.event.ZirconEvent.ComponentRemoved
import org.hexworks.zircon.internal.event.ZirconEvent.LayerChanged
import org.hexworks.zircon.internal.event.ZirconScope
import org.hexworks.zircon.internal.util.PersistentList
import org.hexworks.zircon.platform.factory.PersistentListFactory
import kotlin.jvm.Synchronized

/**
 * A [CompositeLayerStateHolder] can be used to compose an [InternalLayerable]
 * and an [InternalComponentContainer]'s [layerStates].
 */
class CompositeLayerStateHolder(
        private val layerable: InternalLayerable,
        private val components: InternalComponentContainer)
    : InternalLayerable by layerable {

    override var layerStates: PersistentList<LayerState> = PersistentListFactory.create()
        private set

    init {
        refreshLayerStates()
        // will come from the "regular" `layerable`
        Zircon.eventBus.subscribe<LayerChanged>(ZirconScope) {
            refreshLayerStates()
        }
        // these will come form `components`
        Zircon.eventBus.subscribe<ComponentMoved>(ZirconScope) {
            refreshLayerStates()
        }
        Zircon.eventBus.subscribe<ComponentAdded>(ZirconScope) {
            refreshLayerStates()
        }
        Zircon.eventBus.subscribe<ComponentRemoved>(ZirconScope) {
            refreshLayerStates()
        }
    }

    @Synchronized
    private fun refreshLayerStates() {
        layerStates = components.layerStates.addAll(layerable.layerStates)
    }
}
