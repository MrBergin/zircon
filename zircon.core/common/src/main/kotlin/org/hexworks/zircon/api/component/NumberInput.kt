package org.hexworks.zircon.api.component

import org.hexworks.cobalt.databinding.api.event.ChangeEvent
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.events.api.Subscription
import org.hexworks.zircon.api.behavior.Disablable
import org.hexworks.zircon.internal.component.impl.textedit.EditableTextBuffer

interface NumberInput : Component, Disablable {

    var text: String

    /**
     * Current value with respect to the maxValue
     */
    var currentValue: Int
    /**
     * Bindable, current value
     */
    val currentValueProperty: Property<Int>

    fun textBuffer(): EditableTextBuffer

    fun incrementCurrentValue()
    fun decrementCurrentValue()

    /**
     * Callback called when value changes
     */
    fun onChange(fn: (ChangeEvent<Int>) -> Unit): Subscription
}
