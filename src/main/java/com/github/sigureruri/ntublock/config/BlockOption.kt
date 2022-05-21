package com.github.sigureruri.ntublock.config

import org.bukkit.Material

data class BlockOption(
    val block: Material,
    val breakTool: Material,
    val visibleTool: Material,
    val drops: Map<Material, Double>,
    val particleOption: ParticleOption
) {
    sealed class ParticleOption {
        object Hide : ParticleOption()
        data class Show(val updateInterval: Int, val renderDistance: Int) : ParticleOption()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BlockOption) return false

        if (block != other.block) return false

        return true
    }

    override fun hashCode(): Int {
        return block.hashCode()
    }
}