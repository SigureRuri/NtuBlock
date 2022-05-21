package com.github.sigureruri.ntublock

import com.github.sigureruri.ntublock.config.BlockOption
import com.github.sigureruri.ntublock.config.BlockOptionsLoader
import com.github.sigureruri.ntublock.listener.BlockListener
import com.github.sigureruri.ntublock.task.EveryTickTask
import com.github.sigureruri.ntublock.task.ParticleTaskManager
import org.bukkit.plugin.java.JavaPlugin

class NtuBlock : JavaPlugin() {
    companion object {
        lateinit var blockOptions: Set<BlockOption>
            private set

        lateinit var particleTaskManager: ParticleTaskManager
            private set
    }

    override fun onEnable() {
        saveDefaultConfig()

        runCatching {
            BlockOptionsLoader(config).load()
        }.fold(
            onSuccess = { blockOptions = it },
            onFailure = {
                it.printStackTrace()
                return
            }
        )

        particleTaskManager = ParticleTaskManager(this)

        server.pluginManager.registerEvents(BlockListener(), this)

        EveryTickTask().runTaskTimer(this, 1L, 1L)
    }
}