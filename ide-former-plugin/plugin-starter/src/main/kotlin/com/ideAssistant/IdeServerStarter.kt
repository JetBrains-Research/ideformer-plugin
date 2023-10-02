package com.ideAssistant

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import com.intellij.openapi.application.ApplicationStarter
import kotlin.system.exitProcess

object IdeServerStarter : ApplicationStarter {
    @Deprecated("Specify it as `id` for extension definition in a plugin descriptor")
    override val commandName: String
        get() = "ide-server"

    override val requiredModality: Int
        get() = ApplicationStarter.NOT_IN_EDT

    override fun main(args: List<String>) {
        IdeServerStarterCli().main(args.drop(1))
    }
}

class IdeServerStarterCli : CliktCommand() {
    private val input by argument(help = "Path to the project").file(mustExist = true, canBeFile = false)

    override fun run() {
        println("Hello from the server starter")
        exitProcess(0)
    }
}