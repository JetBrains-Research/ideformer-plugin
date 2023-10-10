package org.jetbrains.research.ideFormerPlugin

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.intellij.openapi.application.ApplicationStarter
import com.intellij.openapi.components.service
import org.jetbrains.research.ideFormerPlugin.server.IdeServerService
import org.jetbrains.research.pluginUtilities.openProject.ProjectOpener
import org.jetbrains.research.pluginUtilities.openProject.openAndApply
import org.slf4j.LoggerFactory

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
    private val serverHost: String by argument(help = "Server host")
    private val serverPort: Int by argument(help = "Server port").int()

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun run() {
        val projectOpener = ProjectOpener(null, null)

        projectOpener.openAndApply(input.toPath(), resolve = false) { project ->
            project.service<IdeServerService>().startServer()
            true
        }

        logger.info("IDE server is started")
    }
}