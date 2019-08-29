package org.hexworks.zircon.examples

import org.hexworks.zircon.api.AppConfigs
import org.hexworks.zircon.api.ColorThemes
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Screens
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.extensions.box

object ComponentMoveExample {

    @JvmStatic
    fun main(args: Array<String>) {

        val tileGrid = SwingApplications.startTileGrid(AppConfigs.newConfig()
                .withSize(Sizes.create(60, 30))
                .build())

        val screen = Screens.createScreenFor(tileGrid)

        val panel = Components.panel()
                .withSize(Sizes.create(20, 10))
                .withDecorations(box())
                .build()

        val innerPanel = Components.panel()
                .withSize(Sizes.create(10, 5))
                .withDecorations(box())
                .build()

        innerPanel.addComponent(Components.button()
                .withText("Foo")
                .withPosition(Positions.offset1x1())
                .build())

        panel.addComponent(innerPanel)

        screen.addComponent(panel)

        screen.display()
        screen.applyColorTheme(ColorThemes.adriftInDreams())

        Thread.sleep(2000)

        panel.moveTo(Positions.create(5, 5))

        Thread.sleep(2000)

        innerPanel.moveTo(Positions.create(2, 2))
    }

}
