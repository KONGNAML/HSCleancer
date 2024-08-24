package org.kongit.hscleaner.utils

import org.bukkit.Bukkit
import org.bukkit.World
import org.kongit.hscleaner.configs.Config

object ConfigUtil {

    fun parseStringToMap(input: String): Pair<String, Map<String, String>>? {
        if (input.isBlank()) { return null }

        // 메시지와 옵션을 분리하는 정규식
        val regex = Regex("""(.*?)(\{(.+)?})?$""")
        val matchResult = regex.find(input) ?: return null

        val message = matchResult.groupValues[1].trim()
        val optionsString = matchResult.groupValues.getOrNull(3) ?: ""

        val optionsMap = if (optionsString.isNotEmpty()) {
            optionsString.split(",").associate {
                val (optionKey, optionValue) = it.split("=")
                optionKey.trim() to optionValue.trim()
            }
        } else {
            emptyMap()
        }

        return message to optionsMap
    }


    fun getWorlds(worlds: Config.Worlds) : MutableList<World> {
        val list: MutableList<World> = mutableListOf()
        for (world in Bukkit.getWorlds()) {
            if (worlds.mode) { if (worlds.list.contains(world.name)) { list.add(world) }
            } else { if (!worlds.list.contains(world.name)) { list.add(world) } }
        }
        return list
    }

}