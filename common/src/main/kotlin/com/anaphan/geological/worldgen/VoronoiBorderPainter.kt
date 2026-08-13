package com.anaphan.geological.worldgen

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.chunk.ChunkAccess

/**
 * Shared painter, called from both loaders. Colors the top block of every
 * column by hashed-Voronoi plate, black along the boundary band.
 * Pure visualization — no worldgen hooks, no terrain modification.
 */
object VoronoiBorderPainter {

    private val borderColor = Blocks.CONCRETE.black
    private const val BORDER_THICKNESS = 2.0

    fun paint(level: ServerLevel, chunk: ChunkAccess) {
        VoronoiPlates.worldSeed = level.seed

        val pos: ChunkPos = chunk.pos
        val mutable = BlockPos.MutableBlockPos()

        for (dx in 0 until 16) {
            for (dz in 0 until 16) {
                val x = pos.minBlockX + dx
                val z = pos.minBlockZ + dz

                val fx = x.toDouble()
                val fz = z.toDouble()

                val block = borderColor
                if (VoronoiPlates.isNearBorder(fx, fz, BORDER_THICKNESS)) {
                    mutable.set(x, 319, z)
                    chunk.setBlockState(mutable, block.defaultBlockState(), 0)
                }

            }
        }
    }
}