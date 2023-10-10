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
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class IdeServer(
    private val host: String = "localhost",
    private val port: Int = 8082
) {
    private lateinit var ideStateKeeper: IdeStateKeeper
    private val logger = LoggerFactory.getLogger(javaClass)

    fun startServer(userProject: Project) {
        ideStateKeeper = IdeStateKeeper(userProject)

        embeddedServer(Netty, port = port, host = host) {
            module(ideStateKeeper, logger)
        }.start(wait = false)

        logger.info("Server is started")
    }
}

fun Application.module(ideStateKeeper: IdeStateKeeper, logger: Logger) {
    configureRouting(ideStateKeeper, logger)
}

fun Application.configureRouting(ideStateKeeper: IdeStateKeeper, logger: Logger) {
    routing {
        get("/") {
            logger.info("Server GET root page request is called")
            call.respondText(IdeServerConstants.ROOT_PAGE_TEXT)
            logger.info("Server GET root page request is processed")
        }

        get("/project-modules") {
            logger.info("Server GET project modules request is called")
            val apiMethod = GetProjectModules(ideStateKeeper.userProject)
            apiMethod.execute()
            call.respondText(apiMethod.getExecutionRes())
            logger.info("Server GET project modules request is processed")
        }

        get("/file-kt-methods/{fileName}") {
            val fileName = call.parameters["fileName"] ?: return@get call.respondText(
                text = IdeServerConstants.MISSING_FILENAME,
                status = HttpStatusCode.BadRequest
            )
            logger.info("Server GET file kt methods request for file $fileName is called")

            val apiMethod = GetKtFileKtMethods(ideStateKeeper.curDirectory, fileName)
            apiMethod.execute()
            call.respondText(apiMethod.getExecutionRes())
            logger.info("Server GET file kt methods request for file $fileName is processed")

        }

        get("/list-dir-contents/{dirName?}") {
            val dirName = call.parameters["dirName"] ?: "."
            logger.info("Server GET ls request for dir $dirName is called")

            val apiMethod = ListDirectoryContents(ideStateKeeper.curDirectory, dirName)
            apiMethod.execute()
            call.respondText(apiMethod.getExecutionRes())
            logger.info("Server GET ls request for dir $dirName is processed")
        }

        get("/change-dir/{targetDirName?}") {
            val targetDirName = call.parameters["targetDirName"] ?: "."
            logger.info("Server GET cd result request for dir $targetDirName is called")

            val apiMethod = ChangeDirectory(ideStateKeeper, targetDirName)
            apiMethod.execute()
            call.respondText(apiMethod.getExecutionRes())
            ideStateKeeper.saveReversibleApiCall(apiMethod)
            logger.info("Server GET cd result request for dir $targetDirName is processed")
        }

        post("/post-final-ans") {
            logger.info("Server POST final ans request is called")
            val modelFinalAns = call.receiveText()

            val apiMethod = SaveModelFinalAns(modelFinalAns)
            apiMethod.execute()
            call.respondText(apiMethod.getExecutionRes())
            ideStateKeeper.saveReversibleApiCall(apiMethod)
            logger.info("Server POST final ans request is processed")
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