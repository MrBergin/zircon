package org.hexworks.zircon.api.animation

import com.soywiz.korio.compression.deflate.GZIP
import com.soywiz.korio.compression.uncompress
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.baseName
import kotlinx.coroutines.channels.toList
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.hexworks.zircon.api.builder.animation.AnimationBuilder
import org.hexworks.zircon.api.resource.REXPaintResource
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.internal.animation.AnimationMetadata
import org.hexworks.zircon.internal.animation.impl.DefaultAnimationFrame

inline fun <K, V> MutableMap<K, V>.computeWhenAbsent(key: K, remapper: (K) -> V) = this[key]
        ?: remapper(key).also { this[key] = it }

class AnimationResource {

    companion object {

        /**
         * Loads the data of an animation from the given animation file returns an
         * [AnimationBuilder] which contains the loaded data.
         */
        suspend fun loadAnimationFromStream(zipStream: VfsFile, tileset: TilesetResource): AnimationBuilder {
            val files = unZipIt(zipStream)
            val tileInfoSource = files.first { it.baseName == "animation.json" }.readString()
            val (_, animationData) = Json(JsonConfiguration.Stable).parse(AnimationMetadata.serializer(), tileInfoSource)
            val frameMap = mutableMapOf<Int, DefaultAnimationFrame>()
            val frames = animationData.frameMap.map { frame ->
                val fileName = "${animationData.baseName}${frame.frame}.xp"
                frameMap.computeWhenAbsent(frame.frame) {
                    val frameImage = REXPaintResource.loadREXFile(files.first { it.baseName == fileName })
                    val size = frameImage.toLayerList(tileset).maxBy { it.size }!!.size
                    DefaultAnimationFrame(
                            size = size,
                            layers = frameImage.toLayerList(tileset),
                            repeatCount = frame.repeatCount)
                }
                // we need this trick to not load the file (and create a frame out of it) if it has already been loaded
                frameMap[frame.frame]!!.copy(repeatCount = frame.repeatCount)
            }
            return AnimationBuilder.newBuilder()
                    .withFps(animationData.frameRate)
                    .withLoopCount(animationData.loopCount)
                    .addFrames(frames)
        }
    }
}


/**
 * Takes a GZIP-compressed [ByteArray] and returns it decompressed.
 */
fun decompressGZIPByteArray(compressedData: ByteArray): ByteArray {
    return compressedData.uncompress(GZIP)
}

suspend fun unZipIt(zipSource: VfsFile): List<VfsFile> {
    return zipSource.list().toList()
}
