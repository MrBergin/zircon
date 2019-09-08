package org.hexworks.zircon.examples;

import org.hexworks.zircon.api.*;
import org.hexworks.zircon.api.data.Size;
import org.hexworks.zircon.api.graphics.Layer;
import org.hexworks.zircon.api.grid.TileGrid;
import org.hexworks.zircon.api.resource.REXPaintResource;
import org.hexworks.zircon.api.resource.TilesetResource;
import org.hexworks.zircon.api.screen.Screen;

import java.io.InputStream;
import java.util.List;

public class RexLoaderExample {
    private static final int TERMINAL_WIDTH = 16;
    private static final int TERMINAL_HEIGHT = 16;
    private static final TilesetResource TILESET = CP437TilesetResources.yobbo20x20();
    private static final Size SIZE = Sizes.create(TERMINAL_WIDTH, TERMINAL_HEIGHT);
    private static final InputStream RESOURCE = RexLoaderExample.class.getResourceAsStream("/rex_files/cp437_table.xp");

    public static void main(String[] args) {
        REXPaintResource rex = REXPaintResource.loadREXFile(RESOURCE);

        TileGrid tileGrid = SwingApplications.startTileGrid(AppConfigs.newConfig()
                .withDefaultTileset(CP437TilesetResources.taffer20x20())
                .withSize(SIZE)
                .withDebugMode(true)
                .build());

        final Screen screen = Screens.createScreenFor(tileGrid);
        List<Layer> layers = rex.toLayerList(TILESET);
        for (Layer layer : layers) {
            screen.addLayer(layer);
        }
        screen.display();
    }
}
