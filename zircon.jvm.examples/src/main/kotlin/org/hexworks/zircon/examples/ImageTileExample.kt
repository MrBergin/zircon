package org.hexworks.zircon.examples

import org.hexworks.zircon.api.CharacterTileStrings
import org.hexworks.zircon.api.ImageDictionaryTilesetResources
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.Tiles

object ImageTileExample {

    private val imageDictionary = ImageDictionaryTilesetResources.loadTilesetFromFilesystem("zircon.jvm.examples/src/main/resources/image_dictionary")

    @JvmStatic
    fun main(args: Array<String>) {

        val tileGrid = SwingApplications.startTileGrid()

        tileGrid.draw(CharacterTileStrings.newBuilder()
                .withText("You can see an image tile below...")
                .build())


        val imageTile = Tiles.newBuilder()
                .withTileset(imageDictionary)
                .withName("hexworks_logo.png")
                .buildImageTile()

        // TODO: fix positioning

        tileGrid.draw(imageTile, Positions.create(30, 20))

    }

}
