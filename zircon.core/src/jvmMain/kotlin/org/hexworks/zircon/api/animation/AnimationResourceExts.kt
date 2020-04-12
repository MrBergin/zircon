package org.hexworks.zircon.api.animation

import com.soywiz.korio.file.std.openAsZip
import com.soywiz.korio.stream.openAsync
import kotlinx.coroutines.runBlocking
import org.hexworks.zircon.api.resource.TilesetResource
import java.io.InputStream


fun AnimationResource.Companion.loadAnimationFromStream(inputStream: InputStream, tileset: TilesetResource) = runBlocking {
    val bytes = inputStream.readBytes()
    val zip = bytes.openAsync().openAsZip()
    loadAnimationFromStream(zip, tileset)
}