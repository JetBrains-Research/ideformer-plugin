package org.jetbrains.research.ideFormerPlugin

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import com.intellij.openapi.application.ApplicationStarter
import com.intellij.openapi.components.service
import org.jetbrains.research.ideFormerPlugin.server.IdeServerService
import org.jetbrains.research.pluginUtilities.openProject.ProjectOpener
import org.jetbrains.research.pluginUtilities.openProject.openAndApply
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

class IdeServerStarter : ApplicationStarter {
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
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun run() {
        val projectOpener = ProjectOpener(null, null)

        projectOpener.openAndApply(input.toPath(), resolve = true) { project ->
            project.service<IdeServerService>().startServer()
            true
        }

        logger.info("IDE server is started")
        exitProcess(0)
    }
}