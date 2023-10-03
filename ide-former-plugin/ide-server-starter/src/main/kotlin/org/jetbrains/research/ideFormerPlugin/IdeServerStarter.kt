package org.jetbrains.research.ideFormerPlugin

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import com.intellij.openapi.application.ApplicationStarter
import com.intellij.openapi.util.Disposer
import org.jetbrains.research.pluginUtilities.openProject.ProjectOpener
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

    override fun run() {
        val projectOpener = ProjectOpener(null, null)
        projectOpener.open(input.toPath(), Disposer.newDisposable(), false)

        // TODO: to add server starting

        println("IDE server is started")
        exitProcess(0)
    }
}