package org.jetbrains.research.ideFormerPlugin.server

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
import org.jetbrains.research.ideFormerPlugin.api.*
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper

class IdeServer(
    private val host: String = "localhost",
    private val port: Int = 8082
) {
    private lateinit var ideStateKeeper: IdeStateKeeper

    fun startServer(userProject: Project) {
        ideStateKeeper = IdeStateKeeper(userProject)

        embeddedServer(Netty, port = port, host = host) {
            module(ideStateKeeper)
        }.start(wait = false)

        // TODO: add logging
        println("Server is started")
    }
}

fun Application.module(ideStateKeeper: IdeStateKeeper) {
    configureRouting(ideStateKeeper)
}

fun Application.configureRouting(ideStateKeeper: IdeStateKeeper) {
    routing {
        get("/") {
            call.respondText(IdeServerConstants.ROOT_PAGE_TEXT)
        }

        get("/project-modules") {
            val apiMethod = GetProjectModules(ideStateKeeper.userProject)
            apiMethod.execute()
            call.respondText(apiMethod.getExecutionRes())
        }

        get("/file-kt-methods/{file}") {
            val fileName = call.parameters["file"] ?: return@get call.respondText(
                text = IdeServerConstants.MISSING_FILENAME,
                status = HttpStatusCode.BadRequest
            )

            val apiMethod = GetKtFileKtMethods(ideStateKeeper.curDirectory, fileName)
            apiMethod.execute()
            call.respondText(apiMethod.getExecutionRes())
        }

        post("/list-dir-contents/{dirName?}") {
            val dirName = call.parameters["dirName"] ?: "."

            val apiMethod = ListDirectoryContents(ideStateKeeper.curDirectory, dirName)
            apiMethod.execute()
            call.respondText(apiMethod.getExecutionRes())
        }

        post("/change-dir/{targetDirName?}") {
            val targetDirName = call.parameters["targetDirName"] ?: "."

            val apiMethod = ChangeDirectory(ideStateKeeper, targetDirName)
            apiMethod.execute()
            call.respondText(apiMethod.getExecutionRes())
            ideStateKeeper.saveReversibleApiCall(apiMethod)
        }

        post("/post-final-ans") {
            val modelFinalAns = call.receiveText()

            val apiMethod = SaveModelFinalAns(modelFinalAns)
            apiMethod.execute()
            call.respondText(apiMethod.getExecutionRes())
            ideStateKeeper.saveReversibleApiCall(apiMethod)
        }
    }
}

object IdeServerConstants {
    const val ROOT_PAGE_TEXT = "IDE server"
    const val MISSING_FILENAME = "Missing file name"
}

@Service(Service.Level.PROJECT)
class IdeServerService(private val project: Project) {
    private val ideServer = IdeServer()

    fun startServer() {
        object : Task.Backgroundable(project, "IDE server start") {
            override fun run(indicator: ProgressIndicator) {
                ideServer.startServer(project)
            }
        }.queue()
    }
}