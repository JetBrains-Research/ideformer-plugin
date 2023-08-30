package com.ideassistant

import com.intellij.openapi.components.Service
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

@Suppress("MemberVisibilityCanBePrivate")
class IDEServer {
    private val host = "localhost"
    private val port = 8082

    fun startServer() {
        embeddedServer(Netty, port = port, host = host, module = ::configureRouting)
            .start(wait = false)
        println("Server is started")
    }

    fun configureRouting(application: Application) {
        application.routing {
            get("/") {
                call.respondText("Hello World!")
            }
        }
    }
}

@Service(Service.Level.PROJECT)
class IDEServerService(private val project: Project) {
    private val ideServer = IDEServer()

    fun start() {
        object : Task.Backgroundable(project, "IDE server start") {
            override fun run(indicator: ProgressIndicator) {
                ideServer.startServer()
            }
        }.queue()
    }
}