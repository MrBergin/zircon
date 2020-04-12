import org.hexworks.zircon.api.*
import org.hexworks.zircon.api.application.AppConfig
import org.hexworks.zircon.api.builder.application.AppConfigBuilder
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.data.*
import org.hexworks.zircon.api.game.ProjectionMode
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.graphics.Symbols
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.uievent.KeyCode
import org.hexworks.zircon.api.uievent.KeyboardEventType
import org.hexworks.zircon.api.uievent.Processed
import org.hexworks.zircon.examples.DataBindingExampleKotlinJs
import org.hexworks.zircon.examples.games.TopDownObliqueWorldExample
import org.hexworks.zircon.examples.hexworksSkull
import org.hexworks.zircon.internal.resource.BuiltInCP437TilesetResource

//suspend fun main() {
//    DataBindingExampleKotlinJs().show("Hello, Js!")
//}

suspend fun main() {
    CanvasApplications.buildApplication(AppConfigBuilder().enableBetaFeatures().build()).also { it.start() }.tileGrid.apply {
        start(hexworksSkull(
                position = Position.create(width / 2 - 6, height / 2 - 12),
                tileset = BuiltInCP437TilesetResource.ADU_DHABI_16X16))
    }
}

//suspend fun main(args: Array<String>) {
//
//    val screen = Screen.create(CanvasApplications.startTileGrid(
//            AppConfig.newBuilder()
//                    .enableBetaFeatures()
//                    .withDefaultTileset(CP437TilesetResources.rexPaint20x20())
//                    .withDebugMode(true)
//                    .build()))
//
//    val panel = Components.panel()
//            .withSize(screen.size)
//            .withDecorations(ComponentDecorations.box(boxType = BoxType.DOUBLE, title = "World"))
//            .build()
//
//    val ga = GameComponents.newGameAreaBuilder<Tile, Block<Tile>>()
//            .withActualSize(TopDownObliqueWorldExample.WORLD_SIZE)
//            .withVisibleSize(panel.contentSize.to3DSize(TopDownObliqueWorldExample.VISIBLE_Z_LEVELS))
//            .withProjectionMode(ProjectionMode.TOP_DOWN_OBLIQUE_FRONT)
//            .build()
//
//    val gc = GameComponents.newGameComponentBuilder<Tile, Block<Tile>>()
//            .withGameArea(ga)
//            .withSize(panel.contentSize)
//            .build()
//
//    panel.addComponent(gc)
//    screen.addComponent(panel)
//    screen.display()
//    screen.theme = ColorThemes.forest()
//    screen.handleKeyboardEvents(KeyboardEventType.KEY_PRESSED) { event, _ ->
//        if (event.code == KeyCode.LEFT) {
//            ga.scrollOneLeft()
//        }
//        if (event.code == KeyCode.RIGHT) {
//            ga.scrollOneRight()
//        }
//        if (event.code == KeyCode.UP) {
//            ga.scrollOneBackward()
//        }
//        if (event.code == KeyCode.DOWN) {
//            ga.scrollOneForward()
//        }
//        if (event.code == KeyCode.KEY_U) {
//            ga.scrollOneUp()
//        }
//        if (event.code == KeyCode.KEY_D) {
//            ga.scrollOneDown()
//        }
//        Processed
//    }
//
//    TopDownObliqueWorldExample.addGrass(ga)
//
//    val goblinFront = TopDownObliqueWorldExample.BLOCK_BASE.createCopy().apply {
//        front = Tile.empty().withCharacter('g')
//                .withBackgroundColor(TopDownObliqueWorldExample.GREY)
//                .withForegroundColor(TopDownObliqueWorldExample.BLACK)
//        top = Tile.empty().withCharacter(Symbols.ARROW_DOWN)
//                .withBackgroundColor(TopDownObliqueWorldExample.LIGHT_GREY)
//                .withForegroundColor(TopDownObliqueWorldExample.CREAM)
//    }
//    val trollLegs = TopDownObliqueWorldExample.BLOCK_BASE.createCopy().apply {
//        front = Tile.empty().withCharacter(Symbols.DOUBLE_LINE_VERTICAL)
//                .withBackgroundColor(TileColor.transparent())
//                .withForegroundColor(TopDownObliqueWorldExample.ORANGE)
//    }
//    val trollTorso = TopDownObliqueWorldExample.BLOCK_BASE.createCopy().apply {
//        front = Tile.empty().withCharacter('t')
//                .withBackgroundColor(TopDownObliqueWorldExample.GREY)
//                .withForegroundColor(TopDownObliqueWorldExample.BLACK)
//        top = Tile.empty().withCharacter(Symbols.ARROW_DOWN)
//                .withBackgroundColor(TopDownObliqueWorldExample.LIGHT_GREY)
//                .withForegroundColor(TopDownObliqueWorldExample.CREAM)
//    }
//    ga.setBlockAt(Position3D.create(30, 10, 0), goblinFront)
//
//    ga.setBlockAt(Position3D.create(32, 10, 0), trollLegs)
//    ga.setBlockAt(Position3D.create(32, 10, 1), trollTorso)
//
//
//    TopDownObliqueWorldExample.addHouse(ga, Position.create(5, 5), Size.create(12, 8))
//    TopDownObliqueWorldExample.addHouse(ga, Position.create(40, 0), Size.create(9, 8))
//    TopDownObliqueWorldExample.addHouse(ga, Position.create(25, 20), Size.create(14, 6))
//}