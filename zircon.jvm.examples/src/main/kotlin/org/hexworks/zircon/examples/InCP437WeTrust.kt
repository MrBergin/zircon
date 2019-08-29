package org.hexworks.zircon.examples

import org.hexworks.zircon.api.AppConfigs
import org.hexworks.zircon.api.CP437TilesetResources
import org.hexworks.zircon.api.ColorThemes
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Screens
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.extensions.box
import org.hexworks.zircon.api.extensions.shadow
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.tileset.impl.CP437TileMetadataLoader
import java.util.*

object InCP437WeTrust {

    private val theme = ColorThemes.solarizedLightCyan()

    @JvmStatic
    fun main(args: Array<String>) {

        val tileGrid = SwingApplications.startTileGrid(AppConfigs.newConfig()
                .withSize(Sizes.create(23, 24))
                .withDefaultTileset(CP437TilesetResources.wanderlust16x16())
                .build())

        val screen = Screens.createScreenFor(tileGrid)

        val loader = CP437TileMetadataLoader(16, 16)

        val cp437panel = Components.panel()
                .withSize(Sizes.create(19, 19))
                .withPosition(Positions.create(2, 2))
                .withDecorations(box(BoxType.SINGLE), shadow())
                .withRendererFunction { tileGraphics, _ ->
                    loader.fetchMetadata().forEach { (char, meta) ->
                        tileGraphics.draw(
                                tileToDraw = Tiles.defaultTile()
                                        .withCharacter(char)
                                        .withBackgroundColor(theme.primaryBackgroundColor)
                                        .withForegroundColor(ANSITileColor.values()[Random().nextInt(ANSITileColor.values().size)]),
                                drawAt = Positions.create(meta.x, meta.y)
                                        .plus(Positions.offset1x1()))
                    }
                }.build()

        screen.addComponent(cp437panel)

        val btn = Components.checkBox()
                .withText("In CP437 we trust!")
                .withPosition(Positions.create(1, 22))

        screen.addComponent(btn.build())

        screen.applyColorTheme(theme)

        screen.display()
    }

}
