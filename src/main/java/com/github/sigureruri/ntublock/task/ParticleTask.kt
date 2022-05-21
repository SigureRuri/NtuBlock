package com.github.sigureruri.ntublock.task

import com.github.sigureruri.ntublock.config.BlockOption
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class ParticleTask(private val player: Player, private val blockOption: BlockOption) : BukkitRunnable() {
    override fun run() {
        val particleOption = blockOption.particleOption as? BlockOption.ParticleOption.Show ?: throw IllegalArgumentException("particleOption must be ParticleOption.Show")
        val renderDistance = particleOption.renderDistance
        val location = player.location

        (-renderDistance..renderDistance).map { it + location.blockX }.forEach { x ->
            (-renderDistance..renderDistance).map { it + location.blockY }.forEach { y ->
                (-renderDistance..renderDistance).map { it + location.blockZ }.forEach { z ->
                    val searchedLocation = Location(location.world, x.toDouble(), y.toDouble(), z.toDouble())
                    if (searchedLocation.block.type == blockOption.block) {
                        val locationForParticle = searchedLocation.add(0.5, 0.5, 0.5)
                        player.spawnParticle(Particle.BLOCK_MARKER, locationForParticle, 1, searchedLocation.block.blockData)
                    }
                }
            }
        }
    }
}