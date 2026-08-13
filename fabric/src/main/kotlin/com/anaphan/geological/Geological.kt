package com.anaphan.geological

import com.anaphan.geological.worldgen.VoronoiBorderPainter
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents

fun init() {
    Constants.LOG.info("Hello fabric world from Kotlin")
    CommonObject.init()

    ServerChunkEvents.CHUNK_LOAD.register { world, chunk, _ ->
        VoronoiBorderPainter.paint(world, chunk)
    }
}