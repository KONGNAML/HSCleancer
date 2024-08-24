package org.kongit.hscleaner.configs

import com.github.horangshop.lib.plugin.config.HSConfiguration
import org.bukkit.World
import org.kongit.hscleaner.HSCleaner

class Config : HSConfiguration(HSCleaner.instance,"config.yml",1) {

    data class Worlds(val mode: Boolean,val list: MutableList<String>)
    data class Scheduler(val timer:String,val clear:String,val messages:List<String>)

    companion object{

        lateinit var world:Worlds
            private set
        lateinit var scheduler:Scheduler
            private set

    }

    init {
        super.setup(this)
    }

    private fun init() {


        world = Worlds(

            mode = getString("worlds.mode","whitelist") == "whitelist",
            list = getStringList("worlds.list", listOf("world"))

        )
        scheduler = Scheduler(
            timer = getString("scheduler.timer","0h 30m 0s"),
            clear = getString("scheduler.clear","바닥에 떨어진 아이템 %del% 개가 사라졌습니다."),
            messages = getStringList("scheduler.messages", listOf(
                "5초뒤 바닥청소가 진행됩니다. 조심하세요!{timer=0h29m55s}",
                "4초뒤 바닥청소가 진행됩니다. 조심하세요!{timer=0h29m56s}",
                "3초뒤 바닥청소가 진행됩니다. 조심하세요!{timer=0h29m57s}",
                "2초뒤 바닥청소가 진행됩니다. 조심하세요!{timer=0h29m58s}",
                "1초뒤 바닥청소가 진행됩니다. 조심하세요!{timer=0h29m59s}"
            ))
        )



    }





}