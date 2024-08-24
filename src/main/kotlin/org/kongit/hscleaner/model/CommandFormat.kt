package org.kongit.hscleaner.model

interface CommandFormat {

    fun isCommand() : Boolean?
    fun runCommand()
    fun tabComplete() : MutableList<String>

}