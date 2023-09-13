package com.ideassistant

import com.intellij.openapi.components.Service
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

class IDEServer(
    private val host: String = "localhost",
    private val port: Int = 8082
) {
    fun startServer(userProject: Project) {
        ideStateKeeper.initProject(userProject)

        embeddedServer(Netty, port = port, host = host) {
            module()
        }.start(wait = false)

        // TODO: add logging
        println("Server is started")
    }
}

fun Application.module() {
    configureRouting()
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(IDEServerConstants.ROOT_PAGE_TEXT)
        }

        get("/project-modules") {
            val apiMethod = GetAllProjectModules()
            call.respondText(apiMethod.execute())
        }

        get("/file-kt-methods/{file}") {
            val fileName = call.parameters["file"] ?: return@get call.respondText(
                text = IDEServerConstants.MISSING_FILENAME,
                status = HttpStatusCode.BadRequest
            )

            val apiMethod = GetAllKtFileKtMethods(fileName)
            call.respondText(apiMethod.execute())
        }

        post("/list-dir-contents/{dirName?}") {
            val dirName = call.parameters["dirName"] ?: "."
            val apiMethod = ListDirectoryContents(dirName)
            call.respondText(apiMethod.execute())
        }

        post("/change-dir/{targetDirName?}") {
            val targetDirName = call.parameters["targetDirName"] ?: "."
            val apiMethod = ChangeDirectory(targetDirName)
            call.respondText(apiMethod.execute())
            ideStateKeeper.saveReversibleApiCall(apiMethod)
        }

        post("/post-final-ans") {
            val modelFinalAns = call.receiveText()
            val apiMethod = SaveModelFinalAns(modelFinalAns)
            call.respondText(apiMethod.execute())
            ideStateKeeper.saveReversibleApiCall(apiMethod)
        }
    }
}

object IDEServerConstants {
    const val ROOT_PAGE_TEXT = "IDE server"
    const val MISSING_FILENAME = "Missing file name"
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