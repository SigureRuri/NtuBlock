package com.github.sigureruri.ntublock.task

import com.github.sigureruri.ntublock.NtuBlock
import com.github.sigureruri.ntublock.config.BlockOption
import org.bukkit.entity.Player
import java.util.*

class ParticleTaskManager(private val plugin: NtuBlock) {

    // 一つのアイテムが複数のvisibleItemである可能性があるためList<ParticleTask>にして複数追加できるようにしている
    private val particleTasks = mutableMapOf<UUID, MutableList<ParticleTask>>()

    fun start(player: Player, blockOption: BlockOption) {
        if (blockOption.particleOption !is BlockOption.ParticleOption.Show) throw IllegalArgumentException("blockOption must be ParticleOption.Show")

        ParticleTask(player, blockOption).apply {
            particleTasks.getOrPut(player.uniqueId) { mutableListOf() }
                .add(this)

            runTaskTimer(plugin, 0L, blockOption.particleOption.updateInterval.toLong())
        }
    }

    fun end(uuid: UUID) {
        particleTasks[uuid]?.forEach { it.cancel() }
        particleTasks[uuid]?.removeIf { it.isCancelled }
    }

}