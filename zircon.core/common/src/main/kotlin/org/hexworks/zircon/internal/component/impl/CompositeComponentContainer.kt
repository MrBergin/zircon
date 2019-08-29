package org.hexworks.zircon.internal.component.impl

import org.hexworks.zircon.api.component.ColorTheme
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.ComponentStyleSet
import org.hexworks.zircon.api.component.data.ComponentMetadata
import org.hexworks.zircon.api.component.modal.Modal
import org.hexworks.zircon.api.component.modal.ModalResult
import org.hexworks.zircon.api.component.renderer.impl.DefaultComponentRenderingStrategy
import org.hexworks.zircon.api.data.LayerState
import org.hexworks.zircon.api.uievent.Pass
import org.hexworks.zircon.api.uievent.UIEvent
import org.hexworks.zircon.api.uievent.UIEventResponse
import org.hexworks.zircon.internal.component.InternalComponentContainer
import org.hexworks.zircon.internal.component.renderer.RootContainerRenderer
import org.hexworks.zircon.internal.resource.ColorThemeResource
import org.hexworks.zircon.internal.util.PersistentList
import org.hexworks.zircon.platform.factory.PersistentListFactory
import kotlin.jvm.Synchronized

/**
 * This [InternalComponentContainer] holds a "main" container which holds components
 * and a stack of modal containers which hold modals.
 */
class CompositeComponentContainer(
        private val metadata: ComponentMetadata,
        private val mainContainer: InternalComponentContainer = buildContainer(metadata))
    : InternalComponentContainer {

    override val layerStates: PersistentList<LayerState>
        get() {
            return PersistentListFactory.create<LayerState>()
                    .addAll(containerStack.flatMap { it.layerStates })
        }

    private val containerStack = mutableListOf<InternalComponentContainer>()

    init {
        containerStack.add(mainContainer)
    }

    fun isMainContainerActive() = mainContainer.isActive()

    @Synchronized
    override fun dispatch(event: UIEvent): UIEventResponse {
        return containerStack.lastOrNull()?.dispatch(event) ?: Pass
    }

    @Synchronized
    override fun isActive(): Boolean {
        return containerStack.any(InternalComponentContainer::isActive)
    }

    @Synchronized
    override fun activate() {
        mainContainer.activate()
    }

    @Synchronized
    override fun deactivate() {
        containerStack.forEach {
            it.deactivate()
        }
        containerStack.clear()
        containerStack.add(mainContainer)
    }

    @Synchronized
    override fun addComponent(component: Component) {
        mainContainer.addComponent(component)
    }

    @Synchronized
    override fun removeComponent(component: Component): Boolean {
        return mainContainer.removeComponent(component)
    }

    @Synchronized
    override fun applyColorTheme(colorTheme: ColorTheme): ComponentStyleSet {
        return mainContainer.applyColorTheme(colorTheme)
    }

    @Synchronized
    fun addModal(modal: Modal<out ModalResult>) {
        containerStack.lastOrNull()?.deactivate()
        val modalContainer = buildContainer(metadata)
        modalContainer.activate()
        containerStack.add(modalContainer)
        modalContainer.addComponent(modal)
        modal.onClosed {
            containerStack.lastOrNull()?.let { last ->
                last.deactivate()
                containerStack.remove(last)
                containerStack.lastOrNull()?.activate()
            }
        }
        modal.requestFocus()
    }

    companion object {

        private fun buildContainer(metadata: ComponentMetadata): InternalComponentContainer {
            val renderingStrategy = DefaultComponentRenderingStrategy(
                    decorationRenderers = listOf(),
                    componentRenderer = RootContainerRenderer())
            val container = DefaultComponentContainer(
                    root = RootContainer(
                            componentMetadata = metadata,
                            renderingStrategy = renderingStrategy))
            container.applyColorTheme(ColorThemeResource.EMPTY.getTheme())
            return container
        }
    }
}
