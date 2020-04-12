package org.hexworks.zircon.examples.games

import org.hexworks.zircon.api.Shapes
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.data.*
import org.hexworks.zircon.api.game.GameArea
import org.hexworks.zircon.api.graphics.Symbols
import org.hexworks.zircon.api.modifier.Border
import kotlin.random.Random

object TopDownObliqueWorldExample {

    val WORLD_SIZE = Size3D.create(100, 100, 100)
    const val VISIBLE_Z_LEVELS = 5
    private val random = Random(5643218)

    val BLACK = TileColor.fromString("#140c1c")
    val DARK_BROWN = TileColor.fromString("#442434")
    val PURPLE = TileColor.fromString("#30346d")
    val DARK_GREY = TileColor.fromString("#4e4a4e")
    val BROWN = TileColor.fromString("#854c30")
    val DARK_GREEN = TileColor.fromString("#346524")
    val RED = TileColor.fromString("#d04648")
    val GREY = TileColor.fromString("#757161")
    val BLUE = TileColor.fromString("#597dce")
    val ORANGE = TileColor.fromString("#d27d2c")
    val LIGHT_GREY = TileColor.fromString("#8595a1")
    val LIGHT_GREEN = TileColor.fromString("#6daa2c")
    val CREAM = TileColor.fromString("#d2aa99")
    val TEAL = TileColor.fromString("#6dc2ca")
    val YELLOW = TileColor.fromString("#dad45e")
    val BRIGHT_GREEN = TileColor.fromString("#deeed6")
    val TRANSPARENT = TileColor.transparent()


    private val EMPTY = Tile.empty()
    private val FLOOR = Tile.defaultTile()
            .withCharacter(Symbols.BLOCK_SPARSE)
            .withBackgroundColor(BROWN)
            .withForegroundColor(CREAM)

    private val WALL_TOP = Tile.defaultTile()
            .withCharacter(Symbols.BLOCK_SOLID)
            .withForegroundColor(DARK_GREY)

    private val GLASS = Tile.defaultTile()
            .withCharacter(' ')
            .withBackgroundColor(TileColor.create(red = BLUE.red,
                    green = BLUE.green,
                    blue = BLUE.blue,
                    alpha = 125))

    private val ROOF_TOP = Tile.defaultTile()
            .withCharacter(Symbols.DOUBLE_LINE_HORIZONTAL)
            .withModifiers(Border.newBuilder().withBorderColor(BROWN).build())
            .withBackgroundColor(BROWN)
            .withForegroundColor(RED)

    private val ROOF_FRONT = Tile.defaultTile()
            .withCharacter(Symbols.DOUBLE_LINE_HORIZONTAL)
            .withBackgroundColor(DARK_BROWN)
            .withForegroundColor(BROWN)

    val BLOCK_BASE = Block.newBuilder<Tile>()
            .withContent(EMPTY)
            .withEmptyTile(EMPTY)
            .build()

    private fun roof() = Block.newBuilder<Tile>()
            .withContent(EMPTY)
            .withTop(ROOF_TOP)
            .withFront(ROOF_FRONT)
            .withEmptyTile(EMPTY)
            .build()

    private val GRASS_TILES = listOf(
            Tile.defaultTile()
                    .withCharacter(',')
                    .withBackgroundColor(DARK_GREEN)
                    .withForegroundColor(LIGHT_GREEN),
            Tile.defaultTile()
                    .withCharacter('.')
                    .withBackgroundColor(DARK_GREEN)
                    .withForegroundColor(BRIGHT_GREEN),
            Tile.defaultTile()
                    .withCharacter('"')
                    .withBackgroundColor(DARK_GREEN)
                    .withForegroundColor(LIGHT_GREEN))

    private fun grass() = Block.newBuilder<Tile>()
            .withEmptyTile(Tile.empty())
            .withContent(Tile.empty())
            .withBottom(GRASS_TILES[random.nextInt(GRASS_TILES.size)].let {
                it.withForegroundColor(it.foregroundColor.darkenByPercent(random.nextDouble(.1)))
                        .withBackgroundColor(it.backgroundColor.lightenByPercent(random.nextDouble(.1)))
            })
            .build()

    private val EMPTY_BLOCK = Block.newBuilder<Tile>()
            .withEmptyTile(EMPTY)
            .withContent(EMPTY)
            .build()

    private val FLOOR_BLOCK = Block.newBuilder<Tile>()
            .withEmptyTile(Tile.empty())
            .withContent(EMPTY)
            .withBottom(FLOOR)
            .build()

    private val GLASS_BLOCK_FRONT = Block.newBuilder<Tile>()
            .withContent(EMPTY)
            .withFront(GLASS)
            .withEmptyTile(EMPTY)
            .build()

    private val GLASS_BLOCK_BACK = Block.newBuilder<Tile>()
            .withContent(EMPTY)
            .withBack(GLASS)
            .withEmptyTile(EMPTY)
            .build()

    private fun wallOutside() = Tile.defaultTile()
            .withCharacter(Symbols.DOUBLE_LINE_HORIZONTAL_SINGLE_LINE_CROSS)
            .withBackgroundColor(BROWN.darkenByPercent(random.nextDouble(.15)))
            .withForegroundColor(CREAM.lightenByPercent(random.nextDouble(.15)))

    private fun wallInside() = Tile.defaultTile()
            .withCharacter(Symbols.DOUBLE_LINE_VERTICAL_SINGLE_LINE_CROSS)
            .withBackgroundColor(DARK_GREY.darkenByPercent(random.nextDouble(.25)))
            .withForegroundColor(GREY.lightenByPercent(random.nextDouble(.25)))

    private fun wallFront() = Block.newBuilder<Tile>()
            .withContent(EMPTY)
            .withTop(WALL_TOP)
            .withFront(wallOutside())
            .withBack(wallInside())
            .withEmptyTile(EMPTY)
            .build()

    private fun wallBack() = wallFront().withFlippedAroundY()


    fun addHouse(ga: GameArea<Tile, Block<Tile>>, topLeft: Position, size: Size) {
        require(size.width > 2)
        require(size.height > 2)

        val bottomLeft = topLeft.withRelativeY(size.height - 1)
        val bottomRight = bottomLeft.withRelativeX(size.width - 1)
        val topRight = topLeft.withRelativeX(size.width - 1)

        val houseHeight = 4
        Shapes.buildFilledRectangle(topLeft, size).positions.forEach {
            ga.setBlockAt(it.to3DPosition(0), FLOOR_BLOCK)
        }
        repeat(houseHeight) { lvl ->
            Shapes.buildLine(bottomLeft, bottomRight).positions.forEach { pos ->
                ga.setBlockAt(pos.to3DPosition(lvl), wallFront())
            }
            Shapes.buildLine(topLeft, topRight).positions.forEach { pos ->
                ga.setBlockAt(pos.to3DPosition(lvl), wallBack())
            }
            Shapes.buildLine(topLeft.withRelativeY(1), bottomLeft.withRelativeY(-1)).positions.forEach { pos ->
                ga.setBlockAt(pos.to3DPosition(lvl), wallBack())
            }
            Shapes.buildLine(topRight.withRelativeY(1), bottomRight.withRelativeY(-1)).positions.forEach { pos ->
                ga.setBlockAt(pos.to3DPosition(lvl), wallBack())
            }
        }
        val doorPos = topLeft.withRelativeY(size.height - 1).withRelativeX((size.width - 1) / 2)
        ga.setBlockAt(doorPos.toPosition3D(0), FLOOR_BLOCK)
        ga.setBlockAt(doorPos.toPosition3D(1), EMPTY_BLOCK)
        if (size.width > 8 && houseHeight > 3) {
            val frontWindowPositions = listOf(doorPos.withRelativeX(-2).toPosition3D(1),
                    doorPos.withRelativeX(-2).toPosition3D(2),
                    doorPos.withRelativeX(-3).toPosition3D(1),
                    doorPos.withRelativeX(-3).toPosition3D(2),
                    doorPos.withRelativeX(2).toPosition3D(1),
                    doorPos.withRelativeX(2).toPosition3D(2),
                    doorPos.withRelativeX(3).toPosition3D(1),
                    doorPos.withRelativeX(3).toPosition3D(2))
            val backWindowPositions = frontWindowPositions.map { it.withRelativeY(-size.height + 1) }
            frontWindowPositions.forEach {
                ga.setBlockAt(it, GLASS_BLOCK_FRONT)
            }
            backWindowPositions.forEach {
                ga.setBlockAt(it, GLASS_BLOCK_BACK)
            }
        }
        val roofOffset = topLeft.withRelativeX(-1).withRelativeY(-1)
        val roofSize = size.withRelativeHeight(2).withRelativeWidth(2)
        repeat(size.width.coerceAtMost(size.height) / 2 + 1) { idx ->
            Shapes.buildRectangle(roofOffset, roofSize.minus(Size.create(idx * 2, idx * 2))).positions.forEach {
                ga.setBlockAt(it.plus(roofOffset)
                        .withRelativeX(idx)
                        .withRelativeY(idx)
                        .toPosition3D(houseHeight + idx), roof())
            }
        }
    }

    fun addGrass(ga: GameArea<Tile, Block<Tile>>) {
        ga.actualSize.to2DSize().fetchPositions().forEach { pos ->
            ga.setBlockAt(pos.to3DPosition(0), grass())
        }
    }

}
