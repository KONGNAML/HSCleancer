package org.kongit.hscleaner.utils

import com.github.horangshop.lib.plugin.config.Configurations
import com.github.horangshop.lib.util.common.ComponentUtil
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.bossbar.BossBar.Color
import net.kyori.adventure.title.Title
import net.md_5.bungee.api.ChatMessageType
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.kongit.hscleaner.HSCleaner
import java.time.Duration

object MessageUtil {

    private val plugin = HSCleaner.instance
    private val config: Configurations = plugin.configurations

    fun sendAnnounce(sender: CommandSender, miniMessage: String) {
        val audience: Audience = plugin.adventure.sender(sender)
        audience.sendMessage(config.message.prefix.append(ComponentUtil.formatted(sender,miniMessage)))
    }

    fun Player.sendMessage(miniMessage: String, limit: Int = 0) {
        val bossBarPattern = Regex("<bossbar:(\\w+):(\\d+)>(.*?)</bossbar>", RegexOption.DOT_MATCHES_ALL)
        val actionBarPattern = Regex("<actionbar:(\\d+)>(.*?)</actionbar>", RegexOption.DOT_MATCHES_ALL)
        val titlePattern = Regex("<title:(\\d+):(\\d+):(\\d+)>(.*?)</title>")
        val subtitlePattern = Regex("<subtitle>(.*?)</subtitle>")
        var matched = false

        // BossBar 처리
        bossBarPattern.findAll(miniMessage).forEach { matchResult ->
            val (colorString, time, message) = matchResult.destructured
            val color = Color.valueOf(colorString.toUpperCase())
            val bossBar = BossBar.bossBar(ComponentUtil.miniMessage(message),1F,color,BossBar.Overlay.PROGRESS)
            this.showBossBar(bossBar)
            Bukkit.getScheduler().runTaskLater(HSCleaner.instance, Runnable {
                this.hideBossBar(bossBar)
            }, time.toLong() - 1L)
            matched = true
        }

        // ActionBar 처리
        actionBarPattern.findAll(miniMessage).forEach { matchResult ->
            val (time, message) = matchResult.destructured


            player!!.spigot().sendMessage(
                ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent(
                    ComponentUtil.toString(
                        ComponentUtil.formatted(player,message))))
            Bukkit.getScheduler().runTaskLater(HSCleaner.instance, Runnable {
                player!!.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent(
                        ComponentUtil.toString(
                            ComponentUtil.formatted(player,""))))
            }, time.toLong() - 1)
            matched = true


        }

        // Title 및 Subtitle 처리
        val titleMatchResult = titlePattern.find(miniMessage)
        val subtitleMatchResult = subtitlePattern.find(miniMessage)

        if (titleMatchResult != null) {
            val (fadeIn, stay, fadeOut, title) = titleMatchResult.destructured
            val subtitle = subtitleMatchResult?.groupValues?.get(1) ?: ""
            val kyori = Title.title(
                ComponentUtil.formatted(title),ComponentUtil.formatted(subtitle),Title.Times.times(
                Duration.ofSeconds(fadeIn.toLong()/20),
                Duration.ofSeconds(stay.toLong()/20),
                Duration.ofSeconds(fadeOut.toLong()/20),
            )
            );
            this.showTitle(kyori)
            matched = true
        }

        // 매칭된 패턴이 없을 경우 일반 메시지 처리
        if (!matched) {
            if (limit != 0) {
                val limitedMessage = if (miniMessage.length > limit) miniMessage.substring(0, limit) + "..." else miniMessage
                this.sendMessage(ComponentUtil.miniMessage(limitedMessage))
            } else {
                this.sendMessage(ComponentUtil.miniMessage(miniMessage))
            }
        }
    }

}