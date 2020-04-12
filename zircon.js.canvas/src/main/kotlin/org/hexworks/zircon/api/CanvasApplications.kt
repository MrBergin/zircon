import com.soywiz.korio.file.std.UrlVfs
import org.hexworks.zircon.api.application.AppConfig
import org.hexworks.zircon.api.application.Application
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.internal.application.CanvasApplication
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Location
import kotlin.browser.document
import kotlin.browser.window


object CanvasApplications {
    /**
     * Builds a new [Application] using the given `appConfig`.
     */
    fun buildApplication(appConfig: AppConfig = AppConfig.defaultConfiguration()): CanvasApplication {
        val canvas = document.getElementById("zircon-game") as HTMLCanvasElement

        return CanvasApplication(
                appConfig = appConfig,
                canvas = canvas
        ).also {
            canvas.width = it.tileGrid.widthInPixels
            canvas.height = it.tileGrid.heightInPixels
        }
    }

    fun startTileGrid(appConfig: AppConfig = AppConfig.defaultConfiguration()): TileGrid {
        val canvas = document.getElementById("zircon-game") as HTMLCanvasElement

        return CanvasApplication(appConfig = appConfig, canvas = canvas).also {
            it.start()
            canvas.width = it.tileGrid.widthInPixels
            canvas.height = it.tileGrid.heightInPixels
        }.tileGrid
    }
}
