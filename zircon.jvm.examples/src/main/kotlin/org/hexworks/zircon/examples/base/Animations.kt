package org.hexworks.zircon.examples.base

import com.soywiz.korio.file.std.openAsZip
import com.soywiz.korio.stream.openAsync
import kotlinx.coroutines.runBlocking
import org.hexworks.zircon.api.animation.AnimationResource.Companion.loadAnimationFromStream
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.examples.animations.HexworksSkullExampleJava


fun hexworksSkull(position: Position, tileset: TilesetResource) = runBlocking {
    loadAnimationFromStream(
            zipStream = HexworksSkullExampleJava::class.java.getResource("/animations/skull.zap").readBytes().openAsync().openAsZip(),
            tileset = tileset)
            // 0 means infinite
            .withLoopCount(0).apply {
                for (i in 0 until totalFrameCount) {
                    addPosition(position)
                }
            }.build()
}