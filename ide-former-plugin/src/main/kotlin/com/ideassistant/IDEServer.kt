package com.ideassistant

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
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
    private var userProject: Project? = null

    fun startServer(userProject: Project) {
        this.userProject = userProject

        embeddedServer(Netty, port = port, host = host, module = ::configureRouting)
            .start(wait = false)
        // TODO: add logging
        println("Server is started")
    }

    fun configureRouting(application: Application) {
        application.routing {
            get("/") {
                call.respondText("Hello World!")
            }
        }
    }

    fun startServerClientInteraction(userQuery: String): String {
        val interactionChain = StringBuilder()
        val ideApiExecutorService = userProject!!.service<IDEApiExecutorService>()

        var prevStepInfo = userQuery
        while (true) {
            val modelAPIMethodQuery: IDEApiMethod = LLMSimulator.getAPIQuery(prevStepInfo) ?: break
            interactionChain.append("[API Call Info]: $modelAPIMethodQuery\n")

            val apiCallRes = ideApiExecutorService.executeApiMethod(modelAPIMethodQuery)
            interactionChain.append("[API Call Res]: $apiCallRes\n")

            prevStepInfo = apiCallRes
        }

        return interactionChain.toString()
    }
}

@Service(Service.Level.PROJECT)
class IDEServerService(private val project: Project) {
    private val ideServer = IDEServer()

    fun startServer() {
        object : Task.Backgroundable(project, "IDE server start") {
            override fun run(indicator: ProgressIndicator) {
                ideServer.startServer(project)
            }
        }.queue()
    }

    fun processUserQuery(userQuery: String) = ideServer.startServerClientInteraction(userQuery)
}