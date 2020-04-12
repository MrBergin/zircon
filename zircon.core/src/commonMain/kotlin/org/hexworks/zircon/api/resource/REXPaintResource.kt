package org.hexworks.zircon.api.resource

import com.soywiz.korio.file.VfsFile
import org.hexworks.zircon.api.animation.decompressGZIPByteArray
import org.hexworks.zircon.api.graphics.Layer
import org.hexworks.zircon.api.util.rex.REXFile
import org.hexworks.zircon.internal.config.RuntimeConfig
import kotlin.jvm.JvmStatic

class REXPaintResource constructor(private val rexFile: REXFile) {

    /**
     * Converts this [REXPaintResource] to a list of [Layer]s.
     */
    fun toLayerList(tileset: TilesetResource = RuntimeConfig.config.defaultTileset): List<Layer> {
        return this.rexFile.getLayers().map { it.toLayer(tileset) }.toList()
    }

    companion object {
        suspend fun loadREXFile(source: VfsFile): REXPaintResource {
            return REXPaintResource(REXFile.fromByteArray(decompressGZIPByteArray(source.readBytes())))
        }

        @JvmStatic
        fun loadREXFile(source: ByteArray): REXPaintResource {
            return REXPaintResource(REXFile.fromByteArray(decompressGZIPByteArray(source)))
        }
    }
}
