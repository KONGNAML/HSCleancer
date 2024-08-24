package org.kongit.hscleaner.commands.handler

import com.github.horangshop.lib.plugin.command.CommandData
import com.github.horangshop.lib.plugin.config.Configurations
import org.bukkit.command.CommandSender
import org.kongit.hscleaner.HSCleaner
import org.kongit.hscleaner.configs.Config
import org.kongit.hscleaner.model.CommandFormat
import org.kongit.hscleaner.scheduler.ItemScheduler
import org.kongit.hscleaner.scheduler.ItemScheduler.Companion.runTask
import org.kongit.hscleaner.scheduler.ItemScheduler.Companion.schedulers
import org.kongit.hscleaner.utils.MessageUtil

class ReloadPlugin constructor(private val command: CommandData?,private val sender: CommandSender) : CommandFormat {

    private val config: Configurations = HSCleaner.instance.configurations
    private val plugin = HSCleaner.instance

    override fun isCommand(): Boolean? {
        if (command == null) return null
        if (command.args(0) == config.command.get("reload")) { return true
        } else { return null }
    }

    override fun runCommand() {

        if (sender.isOp) {

            runTask.cancel()
            for (index in schedulers) { index.cancel() }

            plugin.configurations.reload()

            Config()

            ItemScheduler()

            MessageUtil.sendAnnounce(sender,config.message.get("reload"))

            return
        }

        MessageUtil.sendAnnounce(sender,config.message.get("permission"))

    }

    override fun tabComplete(): MutableList<String> {
        return when (command?.length()) {
            1 -> {
                if (sender.isOp) { mutableListOf(config.command.get("reload")) }
                else { mutableListOf() }
            }
            else -> mutableListOf()
        }
    }

}