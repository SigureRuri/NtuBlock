package com.github.sigureruri.ntublock

import com.github.sigureruri.ntublock.config.BlockOption
import com.github.sigureruri.ntublock.config.BlockOptionsLoader
import com.github.sigureruri.ntublock.listener.BlockListener
import org.bukkit.plugin.java.JavaPlugin

class NtuBlock : JavaPlugin() {
    companion object {
        lateinit var blockOptions: Set<BlockOption>
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

        server.pluginManager.registerEvents(BlockListener(), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}