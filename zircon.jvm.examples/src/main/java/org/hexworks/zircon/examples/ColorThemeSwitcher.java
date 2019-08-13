package org.hexworks.zircon.examples;

import kotlin.Unit;
import org.hexworks.zircon.api.AppConfigs;
import org.hexworks.zircon.api.CP437TilesetResources;
import org.hexworks.zircon.api.Components;
import org.hexworks.zircon.api.Functions;
import org.hexworks.zircon.api.Positions;
import org.hexworks.zircon.api.Screens;
import org.hexworks.zircon.api.Sizes;
import org.hexworks.zircon.api.SwingApplications;
import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.CheckBox;
import org.hexworks.zircon.api.component.Header;
import org.hexworks.zircon.api.component.Label;
import org.hexworks.zircon.api.component.Panel;
import org.hexworks.zircon.api.component.RadioButtonGroup;
import org.hexworks.zircon.api.component.RadioButtonGroup.Selection;
import org.hexworks.zircon.api.component.TextArea;
import org.hexworks.zircon.api.data.Size;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.api.grid.TileGrid;
import org.hexworks.zircon.api.resource.TilesetResource;
import org.hexworks.zircon.api.screen.Screen;
import org.hexworks.zircon.internal.resource.ColorThemeResource;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.hexworks.zircon.api.ComponentDecorations.box;
import static org.hexworks.zircon.api.ComponentDecorations.shadow;
import static org.hexworks.zircon.api.Functions.*;

@SuppressWarnings("ALL")
public class ColorThemeSwitcher {

    private static final Size SCREEN_SIZE = Sizes.create(80, 41);
    private static final TilesetResource TILESET = CP437TilesetResources.rogueYun16x16();
    private static final ColorThemeResource THEME = ColorThemeResource.GAMEBOOKERS;

    public static void main(String[] args) {

        TileGrid tileGrid = SwingApplications.startTileGrid(AppConfigs.newConfig()
                .withDefaultTileset(TILESET)
                .withSize(SCREEN_SIZE)
                .build());

        Screen screen = Screens.createScreenFor(tileGrid);

        addScreenTitle(screen, "Color themes");

        AtomicReference<ColorThemeResource> currentTheme = new AtomicReference<>(THEME);
        AtomicReference<Header> currentThemeLabel = new AtomicReference<>(createHeaderForTheme(currentTheme.get()));

        final Size infoPanelSize = SCREEN_SIZE.withHeight(10).withRelativeWidth(-4);

        final Panel infoPanel = Components.panel()
                .withDecorations(box(BoxType.SINGLE, "Components example:"))
                .withSize(infoPanelSize)
                .withPosition(Positions.create(2, 2).relativeToBottomOf(currentThemeLabel.get()))
                .build();

        final Button testButton = Components.button()
                .withText("Button")
                .withPosition(Positions.create(0, 2))
                .build();

        final CheckBox checkBox = Components.checkBox()
                .withText("Checkbox")
                .withPosition(Positions.create(0, 1).relativeToBottomOf(testButton))
                .build();

        final Header header = Components.header()
                .withText("Header")
                .withPosition(Positions.create(0, 1).relativeToBottomOf(checkBox))
                .build();

        final Label label = Components.label()
                .withText("Label")
                .withPosition(Positions.create(8, 0)
                        .relativeToRightOf(testButton))
                .build();

        RadioButtonGroup rbg = Components.radioButtonGroup()
                .withSize(Sizes.create(15, 3))
                .withPosition(Positions.create(0, 1).relativeToBottomOf(label))
                .build();
        rbg.addOption("0", "Option 0");
        rbg.addOption("1", "Option 1");
        rbg.addOption("2", "Option 2");


        final Panel panel = Components.panel()
                .withSize(Sizes.create(20, 6))
                .withDecorations(box(BoxType.SINGLE, "Panel"), shadow())
                .withPosition(Positions.create(10, 0)
                        .relativeToRightOf(label))
                .build();

        TextArea textArea = Components.textArea()
                .withPosition(Positions.create(1, 0).relativeToRightOf(panel))
                .withSize(Sizes.create(20, 6))
                .withText("Text box")
                .build();

        infoPanel.addComponent(currentThemeLabel.get());
        infoPanel.addComponent(testButton);
        infoPanel.addComponent(checkBox);
        infoPanel.addComponent(header);
        infoPanel.addComponent(label);
        infoPanel.addComponent(rbg);
        infoPanel.addComponent(panel);
        infoPanel.addComponent(textArea);

        screen.addComponent(infoPanel);


        final Size themePickerSize = SCREEN_SIZE
                .withRelativeHeight(-infoPanelSize.getHeight() - 4)
                .withWidth(SCREEN_SIZE.getWidth() / 3 - 1);

        final Size smallPanelSize = themePickerSize
                .withRelativeWidth(-2)
                .withHeight(themePickerSize.getHeight() / 2 - 1);

        final Panel solarizedLightPanel = Components.panel()
                .withDecorations(box(BoxType.SINGLE, "Solarized Light"))
                .withPosition(Positions.create(0, 1).relativeToBottomOf(infoPanel))
                .withSize(smallPanelSize)
                .build();
        final Panel solarizedDarkPanel = Components.panel()
                .withDecorations(box(BoxType.SINGLE, "Solarized Dark"))
                .withPosition(Positions.create(0, 1).relativeToBottomOf(solarizedLightPanel))
                .withSize(smallPanelSize)
                .build();
        final Panel zenburnPanel = Components.panel()
                .withDecorations(box(BoxType.SINGLE, "Zenburn"))
                .withPosition(Positions.create(1, 0).relativeToRightOf(solarizedLightPanel))
                .withSize(smallPanelSize)
                .build();
        final Panel monokaiPanel = Components.panel()
                .withDecorations(box(BoxType.SINGLE, "MonokaiLight"))
                .withPosition(Positions.create(1, 0).relativeToRightOf(solarizedDarkPanel))
                .withSize(smallPanelSize)
                .build();
        final Panel otherPanel = Components.panel()
                .withDecorations(box(BoxType.SINGLE, "Other"))
                .withPosition(Positions.create(1, 0).relativeToRightOf(zenburnPanel))
                .withSize(themePickerSize.withRelativeWidth(3).withRelativeHeight(-1))
                .build();


        screen.addComponent(solarizedLightPanel);
        screen.addComponent(solarizedDarkPanel);
        screen.addComponent(zenburnPanel);
        screen.addComponent(monokaiPanel);
        screen.addComponent(otherPanel);

        final List<ColorThemeResource> solarizedLightOptions = Arrays.stream(ColorThemeResource.values())
                .filter(option -> option.name().startsWith("SOLARIZED_LIGHT"))
                .collect(Collectors.toList());
        final List<ColorThemeResource> solarizedDarkOptions = Arrays.stream(ColorThemeResource.values())
                .filter((option) -> option.name().startsWith("SOLARIZED_DARK"))
                .collect(Collectors.toList());
        final List<ColorThemeResource> zenburnOptions = Arrays.stream(ColorThemeResource.values())
                .filter((option) -> option.name().startsWith("ZENBURN"))
                .collect(Collectors.toList());
        final List<ColorThemeResource> monokaiOptions = Arrays.stream(ColorThemeResource.values())
                .filter((option) -> option.name().startsWith("MONOKAI"))
                .collect(Collectors.toList());
        final List<ColorThemeResource> otherOptions = Arrays.stream(ColorThemeResource.values())
                .filter((option) -> !option.name().startsWith("SOLARIZED") &&
                        !option.name().startsWith("MONOKAI") &&
                        !option.name().startsWith("ZENBURN") &&
                        !option.name().startsWith("EMPTY"))
                .collect(Collectors.toList());

        final RadioButtonGroup slOptions = Components.radioButtonGroup()
                .withSize(themePickerSize
                        .withHeight(solarizedLightOptions.size())
                        .withRelativeWidth(-4))
                .build();
        solarizedLightOptions.forEach((option) -> slOptions.addOption(
                option.name(),
                option.name().replace("SOLARIZED_LIGHT_", "")));
        solarizedLightPanel.addComponent(slOptions);

        final RadioButtonGroup sdOptions = Components.radioButtonGroup()
                .withSize(themePickerSize
                        .withHeight(solarizedDarkOptions.size())
                        .withRelativeWidth(-4))
                .build();
        solarizedDarkOptions.forEach((option) -> sdOptions.addOption(
                option.name(),
                option.name().replace("SOLARIZED_DARK_", "")));
        solarizedDarkPanel.addComponent(sdOptions);

        final RadioButtonGroup zbOptions = Components.radioButtonGroup()
                .withSize(themePickerSize
                        .withHeight(zenburnOptions.size())
                        .withRelativeWidth(-4))
                .build();
        zenburnOptions.forEach((option) -> zbOptions.addOption(
                option.name(),
                option.name().replace("ZENBURN_", "")));
        zenburnPanel.addComponent(zbOptions);

        final RadioButtonGroup mOptions = Components.radioButtonGroup()
                .withSize(themePickerSize
                        .withHeight(monokaiOptions.size())
                        .withRelativeWidth(-4))
                .build();
        monokaiOptions.forEach((option) -> mOptions.addOption(
                option.name(),
                option.name().replace("MONOKAI_", "")));
        monokaiPanel.addComponent(mOptions);

        final RadioButtonGroup othOptions = Components.radioButtonGroup()
                .withSize(otherPanel.getSize()
                        .withHeight(otherOptions.size())
                        .withRelativeWidth(-2))
                .build();
        otherOptions.forEach((option) -> othOptions.addOption(
                option.name(),
                option.name()));
        otherPanel.addComponent(othOptions);

        slOptions.onSelection(fromConsumer((selection -> {
            refreshTheme(screen, currentTheme, currentThemeLabel, infoPanel, selection);
        })));
        sdOptions.onSelection(fromConsumer((selection -> {
            refreshTheme(screen, currentTheme, currentThemeLabel, infoPanel, selection);
        })));
        zbOptions.onSelection(fromConsumer((selection -> {
            refreshTheme(screen, currentTheme, currentThemeLabel, infoPanel, selection);
        })));
        mOptions.onSelection(fromConsumer((selection -> {
            refreshTheme(screen, currentTheme, currentThemeLabel, infoPanel, selection);
        })));
        othOptions.onSelection(fromConsumer((selection -> {
            refreshTheme(screen, currentTheme, currentThemeLabel, infoPanel, selection);
        })));

        screen.applyColorTheme(currentTheme.get().getTheme());
        screen.display();

    }

    private static void refreshTheme(Screen screen,
                                     AtomicReference<ColorThemeResource> themeRef,
                                     AtomicReference<Header> labelRef,
                                     Panel infoPanel,
                                     Selection selection) {
        themeRef.set(ColorThemeResource.valueOf(selection.getKey()));
        infoPanel.removeComponent(labelRef.get());
        labelRef.set(createHeaderForTheme(themeRef.get()));
        infoPanel.addComponent(labelRef.get());
        screen.applyColorTheme(themeRef.get().getTheme());
    }

    private static Header createHeaderForTheme(ColorThemeResource currentTheme) {
        return Components.header()
                .withText(currentTheme.name())
                .build();
    }

    private static void addScreenTitle(Screen screen, String title) {
        final Header header = Components.header()
                .withText(title)
                .withPosition(Positions.create(2, 1))
                .build();
        screen.addComponent(header);
    }
}
