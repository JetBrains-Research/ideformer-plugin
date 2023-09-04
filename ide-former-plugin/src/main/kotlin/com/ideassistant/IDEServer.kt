package com.ideassistant

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class IDEServer {
    private val host = "localhost"
    private val port = 8082
    private lateinit var userProject: Project

    fun startServer(userProject: Project) {
        this.userProject = userProject

        fun getAPIMethodRes(apiMethod: IDEApiMethod): String =
            userProject.service<IDEApiExecutorService>().executeApiMethod(apiMethod)

        embeddedServer(Netty, port = port, host = host) {
            routing {
                get("/") {
                    call.respondText("Hello World!")
                }

                get("/project-modules") {
                    val apiMethod = GetAllProjectModules
                    call.respondText(getAPIMethodRes(apiMethod))
                }

                get("/module-files/{module}") {
                    val moduleName = call.parameters["module"] ?: return@get call.respondText(
                        "Missing module",
                        status = HttpStatusCode.BadRequest
                    )

                    val apiMethod = GetAllModuleFiles(moduleName)
                    call.respondText(getAPIMethodRes(apiMethod))
                }

                get("/file-kt-methods/{file}") {
                    val fileName = call.parameters["file"] ?: return@get call.respondText (
                        "Missing file",
                        status = HttpStatusCode.BadRequest
                    )

                    val apiMethod = GetAllModuleFiles(fileName)
                    call.respondText(getAPIMethodRes(apiMethod))
                }
            }
        }.start(wait = false)

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
        val ideApiExecutorService = userProject.service<IDEApiExecutorService>()

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