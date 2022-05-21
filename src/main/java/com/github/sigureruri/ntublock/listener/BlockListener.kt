package com.github.sigureruri.ntublock.listener

import com.github.sigureruri.ntublock.NtuBlock
import com.github.sigureruri.ntublock.extension.blockCenter
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.inventory.ItemStack

class BlockListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onDamageBlock(event: BlockDamageEvent) {
        if (event.player.gameMode != GameMode.SURVIVAL) return

        val itemInHand = event.itemInHand
        val block = event.block

        NtuBlock.blockOptions.forEach { blockOption ->
            if (blockOption.breakTool != itemInHand.type) return@forEach
            if (blockOption.block != block.type) return@forEach

            if (!event.player.hasPermission("ntublock.break.${blockOption.block.toString().lowercase()}")) return@forEach

            block.type = Material.AIR

            with(block.world) {
                blockOption.drops
                    .map {
                        // 100を超えている場合は、百の位分のアイテムを確定でドロップして、%(演算子) 100した確率で追加で一つドロップする
                        it.key to (it.value.toInt() / 100) + if (Math.random() * 100 < it.value % 100) 1 else 0
                    }
                    .filterNot { it.second == 0 }
                    .forEach {
                        dropItem(block.location.blockCenter(), ItemStack(it.first, it.second))
                    }

                playSound(block.location, Sound.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1f, 1f)
                spawnParticle(
                    Particle.BLOCK_CRACK,
                    block.location.blockCenter(),
                    30,
                    0.3,
                    0.3,
                    0.3,
                    Bukkit.createBlockData(blockOption.block)
                )
            }
        }
    }
}