package org.kongit.hscleaner.scheduler

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Item
import org.bukkit.scheduler.BukkitTask
import org.kongit.hscleaner.HSCleaner
import org.kongit.hscleaner.configs.Config.Companion.scheduler
import org.kongit.hscleaner.configs.Config.Companion.world
import org.kongit.hscleaner.utils.ConfigUtil
import org.kongit.hscleaner.utils.MessageUtil.sendMessage

class ItemScheduler {

    companion object {
        lateinit var runTask:BukkitTask
            private set
        val schedulers:MutableList<BukkitTask> = mutableListOf()
    }

    init {
        schedule()
    }


    private fun schedule() {
        val worlds = ConfigUtil.getWorlds(world)
        val timer = timeStringToSeconds(scheduler.timer)
        runTask = Bukkit.getScheduler().runTaskLater(HSCleaner.instance, Runnable {
            for (world: World in worlds) {
                var del:Int = 0
                for (entity in world.entities) {

                    if (entity is Item) { entity.remove();del += entity.itemStack.amount }
                }
                for (player in world.players) { player.sendMessage(scheduler.clear.replace("%del%",del.toString()),0) }
            }
            for (index in schedulers) { index.cancel() }
            schedule()
        }, timer*20L)

        for (messages in scheduler.messages) {

            val pair = ConfigUtil.parseStringToMap(messages)

            schedulers.add(
                Bukkit.getScheduler().runTaskLater(HSCleaner.instance, Runnable {
                    for (world: World in worlds) {
                        for (player in world.players) {
                            player.sendMessage(pair?.first ?: continue,0);
                            if (pair.second["sound"] != null) {
                                player.playSound(Sound.sound(Key.key(pair.second["sound"]!!),Sound.Source.MASTER,1F,1F))
                            }
                        }
                    }
                }, timeStringToSeconds((pair?.second?.get("timer") ?: continue ).toString())*20L)
            )

        }

    }

    private fun timeStringToSeconds(timeString: String): Int {
        val timePattern = Regex("""(?:(\d+)h)?\s*(?:(\d+)m)?\s*(?:(\d+)s)?""")
        val matchResult = timePattern.matchEntire(timeString.trim())

        return if (matchResult != null) {
            val (hours, minutes, seconds) = matchResult.destructured

            val totalSeconds = (hours.toIntOrNull() ?: 0) * 3600 +
                    (minutes.toIntOrNull() ?: 0) * 60 +
                    (seconds.toIntOrNull() ?: 0)

            totalSeconds
        } else {
            0
        }
    }



}