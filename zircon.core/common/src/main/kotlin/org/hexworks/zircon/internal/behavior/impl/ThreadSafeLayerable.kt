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

// TODO: test this thoroughly
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
        index.whenValidIndex {
            layers = layers.set(index, layer)
            layerStates = layerStates.set(index, layer.state)
        }
    }

    @Synchronized
    override fun insertLayerAt(index: Int, layer: Layer) {
        index.whenValidIndex {
            layers = layers.add(index, layer)
            layerStates = layerStates.add(index, layer.state)
        }
    }

    @Synchronized
    override fun insertLayersAt(index: Int, layers: Collection<Layer>) {
        layers.forEachIndexed { idx, layer ->
            insertLayerAt(index + idx, layer)
        }
    }

    @Synchronized
    override fun removeLayerAt(index: Int) {
        index.whenValidIndex {
            layers = layers.removeAt(index)
            layerStates = layerStates.removeAt(index)
        }
    }

    @Synchronized
    override fun removeLayer(layer: Layer) {
        layerStates.indexOfFirst { it.id == layer.id }.whenValidIndex { idx ->
            removeLayerAt(idx)
        }
    }

    @Synchronized
    override fun removeLayers(layers: Collection<Layer>) {
        layers.forEach(::removeLayer)
    }

    @Synchronized
    override fun removeAllLayers() {
        layers = PersistentListFactory.create()
        layerStates = PersistentListFactory.create()
    }

    @Synchronized
    private fun updateLayer(layerState: LayerState) {
        layerStates.indexOfFirst { it.id == layerState.id }.whenValidIndex { idx ->
            layerStates = layerStates.set(idx, layerState)
        }
    }

    private fun Int.whenValidIndex(fn: (Int) -> Unit) {
        if (this in 0 until layers.size) {
            fn(this)
        }
    }
}
