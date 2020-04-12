package org.hexworks.zircon.api.util.rex

import com.soywiz.korio.stream.SyncStream
import com.soywiz.korio.stream.readS32LE
import com.soywiz.korio.stream.readU8
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.internal.util.CP437Utils

/**
 * Represents a CP437 character on a REX Paint [REXLayer].
 */
data class REXCell(private val character: Char,
                   private val foregroundColor: TileColor,
                   private val backgroundColor: TileColor) {

    fun getCharacter() = character

    fun getForegroundColor() = foregroundColor

    fun getBackgroundColor() = backgroundColor

    companion object {
        /**
         * Factory method for [REXCell], which reads out Cell information from a [ByteBuffer].
         */
        fun fromSyncStream(syncStream: SyncStream): REXCell {
            return REXCell(
                    character = CP437Utils.convertCp437toUnicode(syncStream.readS32LE()),
                    foregroundColor = TileColor.create(syncStream.readU8() and 0xFF, syncStream.readU8() and 0xFF, syncStream.readU8() and 0xFF, 255),
                    backgroundColor = TileColor.create(syncStream.readU8() and 0xFF, syncStream.readU8() and 0xFF, syncStream.readU8() and 0xFF, 255))
        }
    }
}