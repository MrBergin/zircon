package org.hexworks.zircon.internal.resource

import org.hexworks.cobalt.core.api.Identifier
import org.hexworks.cobalt.core.platform.factory.IdentifierFactory
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.resource.TilesetResource

/**
 * Contains metadata about a tileset for a given [Tile] type.
 */
internal abstract class BaseTilesetResource : TilesetResource {

    override val id: Identifier = IdentifierFactory.randomIdentifier()

    override fun isCompatibleWith(other: TilesetResource): Boolean {
        return other.tileType == tileType &&
                other.width == width &&
                other.height == height
    }

    override fun toString(): String {
        return "TilesetResource(id=$id, size=$size, tileType=$tileType, tilesetType=$tilesetType, tilesetSourceType=$tilesetSourceType)"
    }


}
