package org.hexworks.zircon.examples.components;

import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.ColorTheme;
import org.hexworks.zircon.api.component.VBox;
import org.hexworks.zircon.api.grid.TileGrid;
import org.hexworks.zircon.api.uievent.ComponentEventType;
import org.hexworks.zircon.examples.components.impl.TwoColumnComponentExample;
import org.jetbrains.annotations.NotNull;

import static org.hexworks.zircon.api.ComponentDecorations.*;
import static org.hexworks.zircon.api.Components.button;
import static org.hexworks.zircon.api.Functions.fromConsumer;

public class ButtonsExampleJava extends TwoColumnComponentExample {

    public static void main(String[] args) {
        new ButtonsExampleJava(createGrid(), createTheme()).show("Buttons Example");
    }

    public ButtonsExampleJava(@NotNull TileGrid tileGrid, @NotNull ColorTheme theme) {
        super(tileGrid, theme);
    }

    @Override
    public void build(VBox box) {
        Button invisible = button()
                .withText("Click Me")
                .withDecorations(side())
                .build();
        invisible.processComponentEvents(ComponentEventType.ACTIVATED, fromConsumer((event) -> {
            invisible.setHidden(true);
        }));

        Button disabled = button()
                .withText("Disabled")
                .build();

        box.addComponents(
                button()
                        .withText("Default")
                        .build(),
                button()
                        .withText("Boxed")
                        .withDecorations(box())
                        .build(),
                button()
                        .withText("Too long name for button")
                        .withDecorations(box(), shadow())
                        .withSize(10, 4)
                        .build(),
                button()
                        .withText("Half Block")
                        .withDecorations(halfBlock(), shadow())
                        .build(),
                invisible, disabled);

        disabled.setDisabled(true);
    }

}
