package com.github.sigureruri.ntublock.extension

import org.bukkit.Location

fun Location.blockCenter() = Location(world, blockX + 0.5, blockY + 0.5, blockZ + 0.5)