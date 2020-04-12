package org.hexworks.zircon.api.resource

import org.hexworks.zircon.api.util.rex.REXFile
import org.hexworks.zircon.internal.util.rex.decompressGZIPByteArray
import java.io.InputStream

/**
 * Loads a REXPaint file from the given input stream.
 */
fun REXPaintResource.Companion.loadREXFile(source: InputStream): REXPaintResource {
    return REXPaintResource(REXFile.fromByteArray(decompressGZIPByteArray(source.readBytes())))
}
