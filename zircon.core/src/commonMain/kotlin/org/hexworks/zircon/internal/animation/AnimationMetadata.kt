package org.hexworks.zircon.internal.animation

import kotlinx.serialization.Serializable

@Serializable
internal data class AnimationMetadata(var type: String = "",
                                      var animationData: AnimationData) {


    @Serializable
    data class AnimationData(var frameCount: Int = -1,
                             var frameRate: Int = -1,
                             var length: Int = -1,
                             var loopCount: Int = -1,
                             var baseName: String = "",
                             var extension: String = "xp",
                             var frameMap: List<Frame> = listOf()) {
    }

    @Serializable
    data class Frame(var frame: Int = -1,
                     var repeatCount: Int = 1) {
    }
}
