@file:Suppress("UNUSED_VARIABLE", "MayBeConstant", "EXPERIMENTAL_API_USAGE")

package org.hexworks.zircon.examples.playground

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.hexworks.zircon.api.DrawSurfaces
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.examples.playground.TilesPlayground.ActorTileComposite.TileCompositeOperation.*
import org.hexworks.zircon.internal.util.PersistentMap
import org.hexworks.zircon.platform.factory.PersistentMapFactory
import java.text.DecimalFormat
import java.util.concurrent.Executors
import kotlin.random.Random
import kotlin.system.exitProcess


object TilesPlayground {

    interface TileComposite {

        fun snapshot(): Snapshot

        fun put(key: Int, value: Int)
    }

    class ReadSafeTileComposite(
            private var state: PersistentMap<Int, Int> = PersistentMapFactory.create())
        : TileComposite {

        override fun snapshot() = Snapshot(state)

        override fun put(key: Int, value: Int) {
            state = state.put(key, value)
        }
    }

    class SynchronizedTileComposite(
            private var state: PersistentMap<Int, Int> = PersistentMapFactory.create())
        : TileComposite {

        override fun snapshot() = Snapshot(state)

        @Synchronized
        override fun put(key: Int, value: Int) {
            state = state.put(key, value)
        }
    }

    class ActorTileComposite(
            private var state: PersistentMap<Int, Int> = PersistentMapFactory.create())
        : TileComposite, CoroutineScope {

        sealed class TileCompositeOperation {
            class Write(val tiles: Map<Int, Int>) : TileCompositeOperation()
        }

        override val coroutineContext = kotlinx.coroutines.Dispatchers.Default

        private val actor = actor<TileCompositeOperation> {
            for (msg in channel) { // iterate over incoming messages
                when (msg) {
                    is Write -> state = state.putAll(msg.tiles)
                }
            }
        }

        override fun snapshot() = Snapshot(state)

        override fun put(key: Int, value: Int) {
            launch {
                actor.send(Write(mapOf(key to value)))
            }
        }
    }

    class MutexTileComposite(
            private var state: PersistentMap<Int, Int> = PersistentMapFactory.create())
        : TileComposite, CoroutineScope {

        override val coroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

        private val mutex = Mutex()

        override fun snapshot() = Snapshot(state)

        @Synchronized
        override fun put(key: Int, value: Int) {
            runBlocking {
                mutex.withLock {
                    state = state.put(key, value)
                }
            }
        }
    }

    data class Snapshot(
            val state: Map<Int, Int>)

    val random = Random(23423)

    val testTile = Tiles.defaultTile().withCharacter('a')

    val testTileComposite = DrawSurfaces.tileImageBuilder()
            .withFiller(testTile)
            .withSize(Sizes.create(20, 20))
            .build()

    val graphicsSize = 100
    val areaSize = graphicsSize * graphicsSize

    @JvmStatic
    fun main(args: Array<String>) {
        val runTime = 10_000_000_000

        ReadWriteBenchmark(
                name = "Single tile write",
                target = ReadSafeTileComposite(),
                readOperation = {
                    it.snapshot()
                },
                writeOperation = {
                    it.put(random.nextInt(areaSize), 1)
                })
                .perform()

        ReadWriteBenchmark(
                name = "Single tile write",
                target = SynchronizedTileComposite(),
                readOperation = {
                    it.snapshot()
                },
                writeOperation = {
                    it.put(random.nextInt(areaSize), 1)
                })
                .perform()

        ReadWriteBenchmark(
                name = "Single tile write",
                target = MutexTileComposite(),
                readOperation = {
                    it.snapshot()
                },
                writeOperation = {
                    it.put(random.nextInt(areaSize), 1)
                })
                .perform()

        ReadWriteBenchmark(
                name = "Single tile write",
                target = ActorTileComposite(),
                readOperation = {
                    it.snapshot()
                },
                writeOperation = {
                    it.put(random.nextInt(areaSize), 1)
                })
                .perform()

        exitProcess(0)
    }

}

class ReadWriteBenchmark<T : Any>(
        private val name: String,
        private val target: T,
        private val runTimeNs: Long = 10_000_000_000,
        private val readOperation: (T) -> Unit,
        private val writeOperation: (T) -> Unit) {

    private val decimalFormat = DecimalFormat().apply {
        groupingSize = 3
    }

    private val producer = Executors.newSingleThreadExecutor()

    @Volatile
    private var reads = 0
    @Volatile
    private var writes = 0

    fun perform() {
        val start = System.nanoTime()
        producer.submit {
            while (true) {
                writeOperation(target)
                writes++
            }
        }
        while (System.nanoTime() < start + runTimeNs) {
            readOperation(target)
            reads++
        }
        val runTimeSecs = runTimeNs / 1000 / 1000 / 1000
        println("$name results for ${target::class.simpleName}. Reads: ${decimalFormat.format(reads / runTimeSecs)}/s," +
                " Writes: ${decimalFormat.format(writes / runTimeSecs)}/s" +
                " in ${runTimeSecs}s.")
        producer.shutdownNow()
    }
}
