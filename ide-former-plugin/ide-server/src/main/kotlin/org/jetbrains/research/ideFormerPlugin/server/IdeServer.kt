package org.jetbrains.research.ideFormerPlugin.server

import com.intellij.openapi.project.Project
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class IdeServer(
    private val host: String,
    private val port: Int
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun startServer(userProject: Project) {
        val ideStateKeeper = IdeStateKeeper(userProject)

        logger.info("Starting server")
        embeddedServer(Netty, port = port, host = host) {
            module(ideStateKeeper, logger)
        }.start(wait = true)
    }
}

fun Application.module(ideStateKeeper: IdeStateKeeper, logger: Logger) {
    configureRouting(ideStateKeeper, logger)
}