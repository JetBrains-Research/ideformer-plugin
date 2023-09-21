package com.ideAssistant

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import com.ideAssistant.server.IdeServerService
import com.intellij.openapi.application.ApplicationStarter
import com.intellij.openapi.components.service
import org.jetbrains.research.pluginUtilities.openRepository.getKotlinJavaRepositoryOpener
import kotlin.system.exitProcess

object PluginRunner : ApplicationStarter {
    @Deprecated("Specify it as `id` for extension definition in a plugin descriptor")
    override val commandName: String
        get() = "IdeAssistantPluginRunner"

    override val requiredModality: Int
        get() = ApplicationStarter.NOT_IN_EDT

    override fun main(args: List<String>) {
        IdeServerStarter().main(args.drop(1))
    }
}

class IdeServerStarter : CliktCommand() {
    private val input by argument(help = "Path to the project").file(mustExist = true, canBeFile = false)

    override fun run() {
        val repositoryOpener = getKotlinJavaRepositoryOpener()

        repositoryOpener.openProjectWithResolve(input.toPath()) { project ->
            project.service<IdeServerService>().startServer()
            true
        }
        exitProcess(0)
    }
}