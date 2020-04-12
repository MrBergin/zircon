package org.hexworks.zircon.api

import com.soywiz.korio.file.std.localVfs
import org.hexworks.zircon.api.resource.REXPaintResource
import kotlin.jvm.JvmStatic

object REXPaintResources {

    /**
     * Loads a REXPaint file from the given path.
     */
    @JvmStatic
    suspend fun loadREXFile(path: String): REXPaintResource {
        return REXPaintResource.loadREXFile(localVfs(path))
    }
}
