package org.hexworks.zircon.internal.data

import org.hexworks.zircon.api.data.base.BaseImageTile
import org.hexworks.zircon.api.resource.TilesetResource

data class DefaultImageTile(
        override val name: String,
        override val tileset: TilesetResource)
    : BaseImageTile() {

    private val cacheKey = "ImageTile(t=${tileset.path},n=$name)"

    override fun createCopy() = copy()

    override fun generateCacheKey() = cacheKey

}
