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
import io.ktor.server.request.*
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
                    call.respondText("IDE server")
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
                    val fileName = call.parameters["file"] ?: return@get call.respondText(
                        "Missing file",
                        status = HttpStatusCode.BadRequest
                    )

                    val apiMethod = GetAllModuleFiles(fileName)
                    call.respondText(getAPIMethodRes(apiMethod))
                }

                post("/post-final-ans") {
                    val modelFinalAns = call.receiveText()
                    val apiMethod = SaveModelFinalAns(modelFinalAns)
                    call.respondText(getAPIMethodRes(apiMethod))
                }
            }
        }.start(wait = false)

        // TODO: add logging
        println("Server is started")
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
}