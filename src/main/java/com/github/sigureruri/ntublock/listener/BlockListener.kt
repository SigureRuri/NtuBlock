package com.github.sigureruri.ntublock.listener

import com.github.sigureruri.ntublock.NtuBlock
import com.github.sigureruri.ntublock.extension.blockCenter
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class BlockListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onInteractBlock(event: PlayerInteractEvent) {
        if (event.useItemInHand() == Event.Result.DENY || event.useInteractedBlock() == Event.Result.DENY) return

        if (event.action != Action.LEFT_CLICK_BLOCK) return
        if (event.hand != EquipmentSlot.HAND) return

        if (event.player.gameMode != GameMode.SURVIVAL) return

        if (!event.hasItem()) return
        if (!event.hasBlock()) return

        val item = event.item!!
        val block = event.clickedBlock!!

        NtuBlock.blockOptions.forEach { blockOption ->
            if (blockOption.breakTool != item.type) return@forEach
            if (blockOption.block != block.type) return@forEach

            block.type = Material.AIR

            with(block.world) {
                blockOption.drops
                    .map {
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