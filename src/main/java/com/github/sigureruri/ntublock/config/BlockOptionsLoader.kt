package com.github.sigureruri.ntublock.config

import org.bukkit.Material
import org.bukkit.configuration.MemoryConfiguration
import org.bukkit.configuration.file.FileConfiguration

class BlockOptionsLoader(private val config: FileConfiguration) {
    @Throws(IllegalStateException::class)
    fun load(): Set<BlockOption> {
        // 値が正常に設定されていない場合でもデフォルトの値を読み込み、正常に動作しているように見えるのを防ぐ
        config.setDefaults(MemoryConfiguration())

        val blocksSection = config.getConfigurationSection("blocks") ?: throw IllegalStateException("ブロック設定が存在していません。")
        val blockOptions = blocksSection.getKeys(false).map { stringBlock ->
            val blockSection = blocksSection.getConfigurationSection(stringBlock) ?: throw IllegalStateException("$stringBlock の設定の書式が正しくありません。")

            val blockMaterial = getMaterialFromString(stringBlock)

            val toolSection =
                blockSection.getConfigurationSection("tool") ?: throw IllegalStateException("$stringBlock のツールが正しく指定されていません。")

            val stringBreakTool =
                toolSection.getString("break") ?: throw IllegalStateException("$stringBlock の破壊ツールが正しく指定されていません。")
            val breakTool = getMaterialFromString(stringBreakTool)

            val stringVisible =
                toolSection.getString("visible") ?: throw IllegalStateException("$stringBlock の表示ツールが正しく指定されていません。")
            val visibleTool = getMaterialFromString(stringVisible)

            val dropsSection = blockSection.getConfigurationSection("drops") ?: throw IllegalStateException("$stringBlock のドロップが正しく指定されていません。")
            val drops = dropsSection.getKeys(false).associate { stringDropMaterial ->
                val material = getMaterialFromString(stringDropMaterial)
                val probability =
                    if (dropsSection.isDouble(stringDropMaterial)) {
                        dropsSection.getDouble(stringDropMaterial)
                    } else {
                        throw IllegalStateException("$stringBlock のドロップ(${stringDropMaterial})の確率が正しく指定されていません。")
                    }
                Pair(material, probability)
            }

            val particleSection = blockSection.getConfigurationSection("particle") ?: throw IllegalStateException("$stringBlock のパーティクル設定が正しく指定されていません。")
            val showParticle =
                if (particleSection.isBoolean("show")) {
                    particleSection.getBoolean("show")
                } else {
                    throw IllegalStateException("$stringBlock のパーティクル表示設定が正しく指定されていません。")
                }
            val particleOption =
                if (showParticle) {
                    val updateInterval =
                        if (particleSection.isInt("updateInterval")) {
                            particleSection.getInt("updateInterval")
                        } else {
                            throw IllegalStateException("$stringBlock のパーティクル設定(updateInterval)が正しく指定されていません。")
                        }
                    val renderDistance =
                        if (particleSection.isInt("renderDistance")) {
                            particleSection.getInt("renderDistance")
                        } else {
                            throw IllegalStateException("$stringBlock のパーティクル設定(renderDistance)が正しく指定されていません。")
                        }

                    BlockOption.ParticleOption.Show(updateInterval, renderDistance)
                } else {
                    BlockOption.ParticleOption.Hide
                }


            BlockOption(
                blockMaterial,
                breakTool,
                visibleTool,
                drops,
                particleOption
            )
        }


        return blockOptions.toSet()
    }

    private fun getMaterialFromString(stringMaterial: String): Material {
        return try {
            Material.valueOf(stringMaterial.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalStateException("$stringMaterial は存在しません。詳しくは https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html をご覧ください。")
        }
    }
}