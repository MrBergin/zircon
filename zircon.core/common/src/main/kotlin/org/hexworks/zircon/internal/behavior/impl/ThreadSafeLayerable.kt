package org.hexworks.zircon.internal.behavior.impl

import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.events.api.subscribe
import org.hexworks.zircon.api.data.LayerState
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.graphics.Layer
import org.hexworks.zircon.internal.Zircon
import org.hexworks.zircon.internal.behavior.InternalLayerable
import org.hexworks.zircon.internal.event.ZirconEvent
import org.hexworks.zircon.internal.event.ZirconScope
import org.hexworks.zircon.platform.factory.PersistentListFactory
import kotlin.jvm.Synchronized

class ThreadSafeLayerable(initialSize: Size) : InternalLayerable {

    override val size: Size = initialSize

    override var layerStates = PersistentListFactory.create<LayerState>()
        private set

    override var layers = PersistentListFactory.create<Layer>()

    init {
        Zircon.eventBus.subscribe<ZirconEvent.LayerChanged>(ZirconScope) { (layerState) ->
            updateLayer(layerState)
        }
    }

    override fun getLayerAt(index: Int): Maybe<Layer> {
        return Maybe.ofNullable(layers[index])
    }

    @Synchronized
    override fun addLayer(layer: Layer) {
        layers = layers.add(layer)
        layerStates = layerStates.add(layer.state)
    }

    @Synchronized
    override fun setLayerAt(index: Int, layer: Layer) {
        layers = layers.set(index, layer)
        layerStates = layerStates.set(index, layer.state)
    }

    @Synchronized
    override fun insertLayerAt(index: Int, layer: Layer) {
        layers = layers.add(index, layer)
        layerStates = layerStates.add(index, layer.state)
    }

    @Synchronized
    override fun insertLayersAt(index: Int, layers: Collection<Layer>) {
        this.layers = this.layers.addAll(index, layers)
    }

    @Synchronized
    override fun removeLayer(layer: Layer) {
        layers = layers.remove(layer)
        layerStates = layerStates.removeAt(layerStates.indexOfFirst { it.id == layer.id })
    }

    @Synchronized
    override fun removeLayerAt(index: Int) {
        layers = layers.removeAt(index)
        layerStates = layerStates.removeAt(index)
    }

    @Synchronized
    override fun removeLayers(layers: Collection<Layer>) {
        this.layers = this.layers.removeAll(layers)
    }

    @Synchronized
    override fun removeAllLayers() {
        layers = PersistentListFactory.create()
        layerStates = PersistentListFactory.create()
    }

    @Synchronized
    private fun updateLayer(layerState: LayerState) {
        val idx = layerStates.indexOfFirst { it.id == layerState.id }
        if (idx >= 0) {
            layerStates = layerStates.set(idx, layerState)
        }
    }
}
