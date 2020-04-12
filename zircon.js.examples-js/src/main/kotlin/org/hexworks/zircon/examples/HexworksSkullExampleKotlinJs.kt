package org.hexworks.zircon.examples

import com.soywiz.korio.file.std.UrlVfs
import com.soywiz.korio.file.std.openAsZip
import com.soywiz.korio.file.std.resourcesVfs
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Fragments
import org.hexworks.zircon.api.animation.AnimationResource
import org.hexworks.zircon.api.application.AppConfig
import org.hexworks.zircon.api.builder.application.AppConfigBuilder
import org.hexworks.zircon.api.component.Container
import org.hexworks.zircon.api.component.HBox
import org.hexworks.zircon.api.component.VBox
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.internal.component.renderer.NoOpComponentRenderer
import org.hexworks.zircon.internal.resource.BuiltInCP437TilesetResource
import org.hexworks.zircon.internal.resource.ColorThemeResource
import kotlin.random.Random

private val RANDOM = Random(42)

private val TILESET_SIZES = listOf(16, 20)
private val TILESET_SIZE = TILESET_SIZES[RANDOM.nextInt(TILESET_SIZES.size)]

val THEME = ColorThemeResource.values()[RANDOM.nextInt(ColorThemeResource.values().size)]
val TILESETS: List<TilesetResource> = BuiltInCP437TilesetResource.values().filter {
    it.width == TILESET_SIZE && it.height == TILESET_SIZE
}
val TILESET = TILESETS[RANDOM.nextInt(TILESETS.size)]

val GRID_SIZE = Size.create(60, 40)

suspend fun hexworksSkull(position: Position, tileset: TilesetResource) =
        AnimationResource.loadAnimationFromStream(
                zipStream = resourcesVfs["/animations/skull.zap"].openAsZip(),
                tileset = tileset)
                .withLoopCount(0).apply {
                    for (i in 0 until totalFrameCount) {
                        addPosition(position)
                    }
                }.build()


abstract class OneColumnComponentExampleKotlinJs(
        size: Size = GRID_SIZE
) : ComponentExampleKotlinJs(size) {

    override fun addExamples(exampleArea: HBox) {
        val box = Components.vbox()
                .withSize(exampleArea.width, exampleArea.height)
                .withComponentRenderer(NoOpComponentRenderer())
                .withSpacing(1)
                .build()
        build(box)
        exampleArea.addComponent(box)
    }
}

class DataBindingExampleKotlinJs : OneColumnComponentExampleKotlinJs() {

    override fun build(box: VBox) {
        val columns = Components.hbox()
                .withSize(box.contentSize)
                .build()
        box.addComponents(columns)

        val columnSize = columns.contentSize.withWidth(columns.width / 2)

        val leftColumn = Components.vbox()
                .withSize(columnSize)
                .build()

        val rightColumn = Components.vbox()
                .withSize(columnSize)
                .build()

        val oneWayBinding = Components.vbox()
                .withSize(columnSize.withHeight(4))
                .withDecorations(ComponentDecorations.box(BoxType.SINGLE, "One-way Binding"))
                .build()
        leftColumn.addComponent(oneWayBinding)

        val check0 = Components.checkBox().withText("Check Me!").build()
        val check1 = Components.checkBox().withText("I am bound!").build()
        check1.selectedProperty.updateFrom(check0.selectedProperty)

        oneWayBinding.addComponents(check0, check1)

        columns.addComponents(leftColumn, rightColumn)
    }

    companion object {
        fun main(args: Array<String>) {
            DataBindingExampleKotlinJs().show("Data Binding Example")
        }
    }
}


abstract class ComponentExampleKotlinJs(
        private val size: Size = GRID_SIZE
) {

    /**
     * Creates the container for the examples.
     */
    private fun createExampleContainer(screen: Screen, title: String): VBox {

        val container = Components.vbox()
                .withSize(size)
                .withSpacing(1)
                .withComponentRenderer(NoOpComponentRenderer())
                .build()

        val heading = Components.hbox()
                .withSize(size.width, 5)
                .build()

        val controls = Components.vbox()
                .withSize(size.width / 2, 5)
                .build()

        controls.addComponent(Components.label().withText("Pick a theme"))

        val themes = ColorThemeResource.values().toList()
        val themeSelector = Fragments.multiSelect(controls.width - 4, themes)
                .withDefaultSelected(THEME)
                .withCallback { _, newTheme ->
                    screen.theme = newTheme.getTheme()
                }.build()
        controls.addFragment(themeSelector)

        controls.addComponent(Components.label())

        controls.addComponent(Components.label().withText("Pick a tileset"))
        val tilesetSelector = Fragments.multiSelect(controls.width - 4, TILESETS)
                .withDefaultSelected(TILESET)
                .withCallback { _, newTileset ->
                    container.tileset = newTileset
                }
                .build()
        controls.addFragment(tilesetSelector)
        heading.addComponents(
                Components.header().withText(title).withSize(size.width / 2, 1).build(),
                controls)
        val exampleArea = Components.hbox()
                .withComponentRenderer(NoOpComponentRenderer())
                .withSize(size.width, size.height - 6)
                .build()
        addExamples(exampleArea)
        container.addComponents(heading, exampleArea)
        return container
    }

    /**
     * Shows this example with the given title on the screen.
     */
    fun show(title: String) {
        val tileGrid = startTileGrid(AppConfig.newBuilder()
                .withDefaultTileset(TILESET)
                .withSize(size.plus(Size.create(2, 2)))
                .build())
        val screen = Screen.create(tileGrid)
        val container: Container = Components.panel()
                .withSize(size)
                .withPosition(Position.offset1x1())
                .withComponentRenderer(NoOpComponentRenderer())
                .build()
        container.addComponent(createExampleContainer(screen, title))
        screen.addComponent(container)
        screen.display()
        screen.theme = THEME.getTheme()
    }

    /**
     * Builds the actual example code using the given box.
     */
    abstract fun build(box: VBox)

    /**
     * Adds the example(s) to the root container according to the layout
     * being used (one column vs two column).
     */
    abstract fun addExamples(exampleArea: HBox)

}

fun startTileGrid(appConfig: AppConfig = AppConfig.defaultConfiguration()): TileGrid {
    return CanvasApplications.buildApplication(appConfig).also {
        it.start()
    }.tileGrid
}