package org.hexworks.zircon.internal.tileset

import com.soywiz.korim.format.readBitmap
import com.soywiz.korim.format.toHtmlNative
import com.soywiz.korio.file.std.resourcesVfs
import kotlinx.coroutines.await
import org.hexworks.cobalt.core.api.UUID
import org.hexworks.cobalt.core.platform.factory.UUIDFactory
import org.hexworks.zircon.api.data.CharacterTile
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.modifier.SimpleModifiers
import org.hexworks.zircon.api.modifier.TextureTransformModifier
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.api.tileset.TextureTransformer
import org.hexworks.zircon.api.tileset.TileTexture
import org.hexworks.zircon.api.tileset.Tileset
import org.hexworks.zircon.api.tileset.impl.CP437TileMetadataLoader
import org.hexworks.zircon.internal.resource.TileType.CHARACTER_TILE
import org.hexworks.zircon.internal.tileset.impl.DefaultTileTexture
import org.hexworks.zircon.internal.tileset.transformer.*
import org.w3c.dom.CanvasImageSource
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.ImageBitmapSource
import kotlin.browser.document
import kotlin.browser.window
import kotlin.reflect.KClass

class Canvas2DCP437Tileset(private val resource: TilesetResource)
    : Tileset<CanvasRenderingContext2D> {

    override val id: UUID = UUIDFactory.randomUUID()
    override val width: Int
        get() = resource.width
    override val height: Int
        get() = resource.height

    private val lookup = CP437TileMetadataLoader(
            width = resource.width,
            height = resource.height)


    override val targetType = CanvasRenderingContext2D::class

    init {
        require(resource.tileType == CHARACTER_TILE) {
            "Can't use a ${resource.tileType.name}-based TilesetResource for" +
                    " a CharacterTile-based tileset."
        }
    }

    override suspend fun drawTile(tile: Tile, surface: CanvasRenderingContext2D, position: Position) {
        val texture = fetchTextureForTile(tile)
        val x = position.x * width
        val y = position.y * height
        surface.drawImage(texture.texture, x.toDouble(), y.toDouble())
    }

    private val cache = mutableMapOf<String, CanvasImageSource>()
    private val anotherCache = mutableMapOf<String, TileTexture<HTMLCanvasElement>>()

    private val canvas = document.createElement("canvas") as HTMLCanvasElement

    private suspend fun fetchTextureForTile(tile: Tile): TileTexture<HTMLCanvasElement> {
        val fixedTile = tile as? CharacterTile ?: kotlin.run {
            throw IllegalArgumentException("A CP437 renderer can only render CharacterTiles. Offending tile: $tile.")
        }
        val meta = lookup.fetchMetaForTile(fixedTile)

        val source = cache.getOrPut(resource.path) {
            @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
            resourcesVfs[resource.path].readBitmap().toHtmlNative().element as CanvasImageSource
        }
        return anotherCache.getOrPut(tile.cacheKey) {
            canvas.width = meta.width
            canvas.height = meta.height
            canvas.getContext("2d").unsafeCast<CanvasRenderingContext2D>().drawImage(
                    source,
                    meta.x * width.toDouble(), meta.y * height.toDouble(), width.toDouble(),
                    height.toDouble(), 0.0, 0.0, width.toDouble(), height.toDouble()
            )

            var tt: TileTexture<HTMLCanvasElement> = DefaultTileTexture(
                    width = width,
                    height = height,
                    texture = canvas)

            TILE_INITIALIZERS.forEach {
                tt = it.transform(tt, fixedTile)
            }
            fixedTile.modifiers.filterIsInstance<TextureTransformModifier>().forEach {
                tt = TEXTURE_TRANSFORMER_LOOKUP[it::class]?.transform(tt, fixedTile) ?: tt
            }
            tt
        }
    }

    companion object {

        private val TILE_INITIALIZERS = listOf(
                Canvas2DTextureCloner(),
                CanvasDTextureColorizer())

        val TEXTURE_TRANSFORMER_LOOKUP: Map<KClass<out TextureTransformModifier>, TextureTransformer<HTMLCanvasElement>> = mapOf(
                SimpleModifiers.Underline::class to Canvas2DUnderlineTransformer(),
                SimpleModifiers.VerticalFlip::class to Canvas2DVerticalFlipper(),
                SimpleModifiers.Blink::class to Canvas2DNoOpTransformer(),
                SimpleModifiers.HorizontalFlip::class to Canvas2DHorizontalFlipper()
                /*SimpleModifiers.CrossedOut::class to Java2DCrossedOutTransformer(),
                SimpleModifiers.Hidden::class to Java2DHiddenTransformer(),
                Glow::class to Java2DGlowTransformer(),
                Border::class to Java2DBorderTransformer(),
                Crop::class to Java2DCropTransformer(),
                RayShade::class to Java2DRayShaderTransformer(),
                TileCoordinate::class to Java2DTileCoordinateTransformer()*/).toMap()

    }
}
