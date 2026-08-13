package com.anaphan.geological

import com.anaphan.geological.worldgen.VoronoiBorderPainter
import net.minecraft.server.level.ServerLevel
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.level.ChunkEvent

@Mod(Constants.MOD_ID)
class Geological(eventBus: IEventBus, modContainer: ModContainer) {
    init {
        Constants.LOG.info("Hello NeoForge world from Kotlin!")
        CommonObject.init()

        NeoForge.EVENT_BUS.addListener(::onChunkLoad)
    }

    private fun onChunkLoad(event: ChunkEvent.Load) {
        val level = event.level
        if (level is ServerLevel) {
            VoronoiBorderPainter.paint(level, event.chunk)
        }
    }
}