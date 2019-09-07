package org.hexworks.zircon.examples;

import org.hexworks.zircon.api.*;
import org.hexworks.zircon.api.component.ComponentAlignment;
import org.hexworks.zircon.api.component.HBox;
import org.hexworks.zircon.api.component.VBox;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.api.grid.TileGrid;
import org.hexworks.zircon.api.screen.Screen;

import static org.hexworks.zircon.api.ComponentDecorations.box;
import static org.hexworks.zircon.api.ComponentDecorations.shadow;
import static org.hexworks.zircon.api.GraphicalTilesetResources.nethack16x16;

public class StatsPanelExample {

    public static void main(String[] args) {

        final TileGrid tileGrid = SwingApplications.startTileGrid(AppConfigs.newConfig()
                .withDefaultTileset(CP437TilesetResources.rexPaint16x16())
                .build());

        final Screen screen = Screens.createScreenFor(tileGrid);

        final VBox panel = Components.vbox()
                .withDecorations(box(BoxType.SINGLE, "Sel Darkstrom"), shadow())
                .withSize(20, 20)
                .withAlignmentWithin(screen, ComponentAlignment.CENTER)
                .build();

        panel.addComponent(Components.label().withSize(1, 1)); // spacer
        panel.addComponent(Components.header().withText("Stats"));

        HBox attack = Components.hbox()
                .withSize(panel.getContentSize().getWidth(), 1)
                .build();
        attack.addComponent(Components.icon().withIcon(Tiles.newBuilder()
                .withName("Short sword")
                .withTileset(nethack16x16())
                .buildGraphicTile()).build());
        attack.addComponent(Components.label().withText("Attack:5"));
        panel.addComponent(attack);

        HBox defense = Components.hbox()
                .withSize(panel.getContentSize().getWidth(), 1)
                .build();
        defense.addComponent(Components.icon()
                .withIcon(Tiles.newBuilder()
                        .withName("Small shield")
                        .withTileset(nethack16x16())
                        .buildGraphicTile()).build());
        defense.addComponent(Components.label()
                .withText("Defense:4"));
        panel.addComponent(defense);

        panel.addComponent(Components.label().withSize(1, 1));
        panel.addComponent(Components.header().withText("Info"));
        panel.addComponent(Components.textBox(panel.getContentSize().getWidth())
                .addParagraph("A short, sturdy creature fond of drink and industry.")
                .addParagraph("Abilities:")
                .addListItem("Drink")
                .addListItem("Dig")
                .addListItem("Sleep")
                .build());

        screen.addComponent(panel);
        screen.applyColorTheme(ColorThemes.zenburnVanilla());
        screen.display();

    }


}
