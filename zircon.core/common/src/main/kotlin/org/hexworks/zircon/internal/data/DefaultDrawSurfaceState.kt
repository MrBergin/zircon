package org.hexworks.zircon.internal.data

import org.hexworks.zircon.api.data.DrawSurfaceState
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.resource.TilesetResource

data class DefaultDrawSurfaceState(
        override val tiles: Map<Position, Tile>,
        override val tileset: TilesetResource,
        override val size: Size) : DrawSurfaceState
