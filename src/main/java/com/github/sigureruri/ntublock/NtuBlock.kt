package com.github.sigureruri.ntublock

import com.github.sigureruri.ntublock.config.BlockOption
import com.github.sigureruri.ntublock.config.BlockOptionsLoader
import org.bukkit.plugin.java.JavaPlugin

class NtuBlock : JavaPlugin() {
    lateinit var blockOptions: Set<BlockOption>

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
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}