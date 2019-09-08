package org.hexworks.zircon.examples;

import org.hexworks.zircon.api.*;
import org.hexworks.zircon.api.data.Size;
import org.hexworks.zircon.api.grid.TileGrid;
import org.hexworks.zircon.api.resource.TilesetResource;

import java.util.Random;

public class GraphicTilesetExample {

    private static final int TERMINAL_WIDTH = 50;
    private static final int TERMINAL_HEIGHT = 24;
    private static final Size SIZE = Sizes.create(TERMINAL_WIDTH, TERMINAL_HEIGHT);
    private static final TilesetResource TILESET = GraphicalTilesetResources.nethack16x16();
    private static final String[] NAMES = new String[]{
            "Giant ant",
            "Killer bee",
            "Fire ant",
            "Werewolf",
            "Dingo",
            "Hell hound pup",
            "Tiger",
            "Gremlin"
    };
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {

        TileGrid tileGrid = SwingApplications.startTileGrid(
                AppConfigs.newConfig().withSize(SIZE).build());

        for (int row = 0; row < TERMINAL_HEIGHT; row++) {
            for (int col = 0; col < TERMINAL_WIDTH; col++) {
                final String name = NAMES[RANDOM.nextInt(NAMES.length)];
                tileGrid.draw(Tiles.newBuilder()
                                .withName(name)
                                .withTileset(TILESET)
                                .buildGraphicalTile(),
                        Positions.create(col, row));
            }
        }

    }

}
