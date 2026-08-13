package com.anaphan.geological.worldgen

import kotlin.math.floor
import kotlin.math.sqrt

/**
 * Infinite, stateless, hashed/jittered Voronoi diagram.
 * No plate list is ever stored — every query is answered on the spot
 * from the world seed, at a fixed cost of 9 hash evaluations.
 */
object VoronoiPlates {

    const val CELL_SIZE = 75.0

    /** Set once from the world seed so plates are reproducible per-world. */
    var worldSeed = 0L

    private fun hash01(cellX: Int, cellZ: Int, salt: Int): Double {
        var h = cellX.toLong()
        h = h * -0x61c8864680b583ebL xor cellZ.toLong()
        h = h * -0x61c8864680b583ebL xor salt.toLong()
        h = h * -0x61c8864680b583ebL xor worldSeed
        h = h xor (h ushr 33)
        h *= -0x61c8864680b583ebL
        h = h xor (h ushr 29)
        h *= -0x319db2ce7bb6a8edL
        h = h xor (h ushr 32)
        return ((h ushr 11).toDouble()) / (1L shl 53).toDouble()
    }

    private fun cellOf(coord: Double): Int = floor(coord / CELL_SIZE).toInt()

    private fun seedPoint(cellX: Int, cellZ: Int): Pair<Double, Double> {
        val jx = hash01(cellX, cellZ, 0)
        val jz = hash01(cellX, cellZ, 1)
        return (cellX + jx) * CELL_SIZE to (cellZ + jz) * CELL_SIZE
    }

    fun plateId(cellX: Int, cellZ: Int): Long =
        (cellX.toLong() shl 32) xor (cellZ.toLong() and 0xFFFFFFFFL) xor (hash01(cellX, cellZ, 2) * 1_000_000).toLong()

    data class Nearest(val cellX: Int, val cellZ: Int, val dist: Double, val secondDist: Double)

    fun nearest(x: Double, z: Double): Nearest {
        val baseCx = cellOf(x)
        val baseCz = cellOf(z)
        var bestCx = baseCx
        var bestCz = baseCz
        var bestDist = Double.MAX_VALUE
        var secondDist = Double.MAX_VALUE

        for (dx in -1..1) {
            for (dz in -1..1) {
                val cx = baseCx + dx
                val cz = baseCz + dz
                val (sx, sz) = seedPoint(cx, cz)
                val ddx = sx - x
                val ddz = sz - z
                val d = sqrt(ddx * ddx + ddz * ddz)
                if (d < bestDist) {
                    secondDist = bestDist
                    bestDist = d
                    bestCx = cx
                    bestCz = cz
                } else if (d < secondDist) {
                    secondDist = d
                }
            }
        }
        return Nearest(bestCx, bestCz, bestDist, secondDist)
    }

    fun isNearBorder(x: Double, z: Double, thickness: Double): Boolean {
        val r = nearest(x, z)
        return (r.secondDist - r.dist) < thickness
    }
}