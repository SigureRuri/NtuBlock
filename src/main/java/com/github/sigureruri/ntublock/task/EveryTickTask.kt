package com.github.sigureruri.ntublock.task

import com.github.sigureruri.ntublock.NtuBlock
import com.github.sigureruri.ntublock.config.BlockOption
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class EveryTickTask : BukkitRunnable() {
    private val lastHanded = mutableMapOf<UUID, Material>()

    override fun run() {
        lastHanded
            .map { it.key }
            .forEach {
                val player = Bukkit.getPlayer(it)
                if (player == null || !player.isOnline || player.gameMode != GameMode.SURVIVAL) {
                    lastHanded.remove(it)
                    NtuBlock.particleTaskManager.end(it)
                }
            }

        Bukkit.getOnlinePlayers()
            .filter { it.gameMode == GameMode.SURVIVAL }
            .forEach { player ->
                val materialOfHavingItem = player.inventory.itemInMainHand.type

                if (lastHanded[player.uniqueId] != materialOfHavingItem) {
                    lastHanded[player.uniqueId] = materialOfHavingItem

                    NtuBlock.particleTaskManager.end(player.uniqueId)

                    NtuBlock.blockOptions
                        .filter { it.particleOption is BlockOption.ParticleOption.Show }
                        .filter { it.visibleTool == materialOfHavingItem }
                        .forEach {
                            NtuBlock.particleTaskManager.start(player, it)
                        }
                }
            }
    }
}