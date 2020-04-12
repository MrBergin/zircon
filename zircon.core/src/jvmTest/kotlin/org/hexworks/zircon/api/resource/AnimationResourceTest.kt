package org.hexworks.zircon.api.resource

import com.soywiz.korio.file.std.openAsZip
import com.soywiz.korio.stream.openAsync
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.hexworks.zircon.api.animation.AnimationResource
import org.hexworks.zircon.api.application.AppConfig
import org.hexworks.zircon.internal.resource.BuiltInCP437TilesetResource
import org.junit.Test
import java.io.File

class AnimationResourceTest {

    @Test
    fun shouldProperlyLoadAnimationFile() = runBlocking {
        AppConfig.newBuilder().enableBetaFeatures().build()
        val result = AnimationResource.loadAnimationFromStream(
                zipStream = File("src/jvmTest/resources/animations/skull.zap").readBytes().openAsync().openAsZip(),
                tileset = BuiltInCP437TilesetResource.ADU_DHABI_16X16)
        Assertions.assertThat(result).isNotNull()
        Unit
    }
}
