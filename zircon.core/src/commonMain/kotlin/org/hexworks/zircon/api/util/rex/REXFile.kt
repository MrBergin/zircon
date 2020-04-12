package org.hexworks.zircon.api.util.rex

import com.soywiz.korio.stream.*

/**
 * Represents a REX Paint File, which contains version and [REXLayer] information.
 */
data class REXFile(private val version: Int,
                   private val numberOfLayers: Int,
                   private val layers: List<REXLayer>) {

    fun getVersion() = version

    fun getNumberOfLayers() = numberOfLayers

    fun getLayers() = layers

    companion object {
        /**
         * Factory method for [REXFile]. It takes an uncompressed [ByteArray] argument and reads out the REX Paint
         * [REXFile] object as defined in the <a href="http://www.gridsagegames.com/rexpaint/manual.txt">REX
         * Paint manual</a>.
         */
        fun fromByteArray(data: ByteArray): REXFile {
            val syncStream = MemorySyncStream(data)

            val version = syncStream.readS32LE()
            val numberOfLayers = syncStream.readS32LE()

            val layers: MutableList<REXLayer> = mutableListOf()
            for (i in 0 until numberOfLayers) {
                layers.add(REXLayer.fromSyncStream(syncStream))
            }

            return REXFile(version, numberOfLayers, layers)
        }
    }
}