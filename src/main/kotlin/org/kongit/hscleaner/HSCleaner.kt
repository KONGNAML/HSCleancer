package org.kongit.hscleaner

import com.github.horangshop.lib.plugin.HSPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.kongit.hscleaner.commands.Commands
import org.kongit.hscleaner.configs.Config
import org.kongit.hscleaner.scheduler.ItemScheduler

class HSCleaner : HSPlugin() {

    companion object {
        lateinit var instance: HSCleaner
            private set
    }

    override fun enable() {
        instance = this
        Config()
        ItemScheduler()
        registerCommand(Commands())
    }

}
