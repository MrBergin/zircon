package org.hexworks.zircon.internal.application

import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import org.hexworks.zircon.api.application.AppConfig
import org.hexworks.zircon.api.application.Application
import org.hexworks.zircon.internal.grid.InternalTileGrid
import org.hexworks.zircon.internal.grid.ThreadSafeTileGrid
import org.hexworks.zircon.internal.renderer.VirtualRenderer
import java.util.concurrent.Executors

class VirtualApplication(private val config: AppConfig,
                         override val tileGrid: InternalTileGrid = ThreadSafeTileGrid(
                                 initialTileset = config.defaultTileset,
                                 initialSize = config.size))
    : BaseApplication(config, tileGrid, Executors.newSingleThreadExecutor().asCoroutineDispatcher() + SupervisorJob()) {

    override val renderer = VirtualRenderer(tileGrid)

    companion object {

        fun create(appConfig: AppConfig = AppConfig.defaultConfiguration()): Application {
            return VirtualApplication(appConfig)
        }

    }
}
