package org.hexworks.zircon.internal.uievent

import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.uievent.MouseEventType
import org.hexworks.zircon.api.uievent.UIEventPhase
import org.hexworks.zircon.internal.config.RuntimeConfig
import org.hexworks.zircon.internal.grid.InternalTileGrid
import java.awt.GraphicsEnvironment
import java.awt.MouseInfo
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent

open class MouseEventListener(
        private val fontWidth: Int,
        private val fontHeight: Int,
        private val tileGrid: InternalTileGrid) : MouseAdapter() {

    private val logger = LoggerFactory.getLogger(this::class)

    private var lastMouseLocation = Position.unknown()

    override fun mouseClicked(e: MouseEvent) {
        if (GraphicsEnvironment.isHeadless().not() &&
                MouseInfo.getNumberOfButtons() > 2 &&
                e.button == MouseEvent.BUTTON2 &&
                RuntimeConfig.config.isClipboardAvailable) {
            pasteSelectionContent()
        }
        processMouseEvent(MouseEventType.MOUSE_CLICKED, e)
    }

    override fun mouseReleased(e: MouseEvent) {
        processMouseEvent(MouseEventType.MOUSE_RELEASED, e)
    }

    override fun mouseMoved(e: MouseEvent) {
        processMouseEvent(MouseEventType.MOUSE_MOVED, e)
    }

    override fun mouseEntered(e: MouseEvent) {
        processMouseEvent(MouseEventType.MOUSE_ENTERED, e)
    }

    override fun mouseExited(e: MouseEvent) {
        processMouseEvent(MouseEventType.MOUSE_EXITED, e)
    }

    override fun mouseDragged(e: MouseEvent) {
        processMouseEvent(MouseEventType.MOUSE_DRAGGED, e)
    }

    override fun mousePressed(e: MouseEvent) {
        processMouseEvent(MouseEventType.MOUSE_PRESSED, e)
    }

    override fun mouseWheelMoved(e: MouseWheelEvent) {
        val actionType = if (e.preciseWheelRotation > 0) {
            MouseEventType.MOUSE_WHEEL_ROTATED_DOWN
        } else {
            MouseEventType.MOUSE_WHEEL_ROTATED_UP
        }
        (0..e.preciseWheelRotation.toInt()).forEach {
            processMouseEvent(actionType, e)
        }
    }

    private fun processMouseEvent(eventType: MouseEventType, e: MouseEvent) {
        try {
            val position = Position.create(
                    x = Math.max(0, e.x.div(fontWidth)),
                    y = Math.max(0, e.y.div(fontHeight)))
            org.hexworks.zircon.api.uievent.MouseEvent(
                    type = eventType,
                    button = e.button,
                    position = position).let {
                if (mouseMovedToNewPosition(eventType, position)
                                .or(isNotMoveEvent(eventType))) {
                    lastMouseLocation = position
                    logger.debug("Processing Mouse Event: $it.")
                    tileGrid.process(it, UIEventPhase.TARGET)
                }
            }
        } catch (e: Exception) {
            logger.error("Position for mouse event '$e' was out of bounds. It is dropped.")
            e.printStackTrace()
        }
    }

    private fun isNotMoveEvent(eventType: MouseEventType) = eventType != MouseEventType.MOUSE_MOVED

    private fun mouseMovedToNewPosition(eventType: MouseEventType, position: Position) =
            eventType == MouseEventType.MOUSE_MOVED && position != lastMouseLocation

    private fun pasteSelectionContent() {
        Toolkit.getDefaultToolkit().systemSelection?.let {
            injectStringAsKeyboardEvents(it.getData(DataFlavor.stringFlavor) as String, tileGrid)
        }
    }

}
