package org.jetbrains.research.ideFormerPlugin.server

import com.google.gson.Gson
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

        logger.info("Starting server")
        embeddedServer(Netty, port = port, host = host) {
            module(ideStateKeeper, logger)
        }.start(wait = true)
    }
}

data class ServerAnswer(val serverAnswer: String)

fun Application.module(ideStateKeeper: IdeStateKeeper, logger: Logger) {
    configureRouting(ideStateKeeper, logger)
}

fun Application.configureRouting(ideStateKeeper: IdeStateKeeper, logger: Logger) {
    install(IgnoreTrailingSlash)
    val gson = Gson()

    routing {
        get("/") {
            logger.info("Server GET root page request is called")
            val serverAnswer = ServerAnswer(IdeServerConstants.ROOT_PAGE_TEXT)
            call.respondText(gson.toJson(serverAnswer))
            logger.info("Server GET root page request is processed")
        }

        get("/ide-api-list") {
            logger.info("Server GET ide api list request is called")

            val apiDescriptions =
                IdeServer::class.java.classLoader.getResourceAsStream("ideApiDescriptions/ideApiDescriptions.json")!!
                    .bufferedReader().readText()
            apiDescriptions.ifEmpty { IdeServerConstants.NO_API_AVAILABLE }

            val serverAnswer = ServerAnswer(apiDescriptions)
            call.respondText(gson.toJson(serverAnswer))
            logger.info("Server GET ide api list request is processed")
        }

        get("/project-modules") {
            logger.info("Server GET project modules request is called")
            val apiMethod = GetProjectModules(ideStateKeeper.userProject)
            apiMethod.execute()

            val serverAnswer = ServerAnswer(apiMethod.getExecutionRes())
            call.respondText(gson.toJson(serverAnswer))
            logger.info("Server GET project modules request is processed")
        }

        get("/file-kt-methods/{fileName}") {
            val fileName = call.parameters["fileName"] ?: return@get call.respondText(
                text = IdeServerConstants.MISSING_FILENAME,
                status = HttpStatusCode.BadRequest
            )
            logger.info("Server GET file kt methods request for file '$fileName' is called")

            val apiMethod = GetKtFileKtMethods(ideStateKeeper.curDirectory, fileName)
            apiMethod.execute()

            val serverAnswer = ServerAnswer(apiMethod.getExecutionRes())
            call.respondText(gson.toJson(serverAnswer))
            logger.info("Server GET file kt methods request for file '$fileName' is processed")

        }

        get("/list-dir-contents/{dirName?}") {
            val dirName = call.parameters["dirName"] ?: "."
            logger.info("Server GET ls request for dir '$dirName' is called")

            val apiMethod = ListDirectoryContents(ideStateKeeper.curDirectory, dirName)
            apiMethod.execute()

            val serverAnswer = ServerAnswer(apiMethod.getExecutionRes())
            call.respondText(gson.toJson(serverAnswer))
            logger.info("Server GET ls request for dir '$dirName' is processed")
        }

        get("/change-dir/{targetDirName?}") {
            val targetDirName = call.parameters["targetDirName"] ?: "."
            logger.info("Server GET cd result request for dir '$targetDirName' is called")

            val apiMethod = ChangeDirectory(ideStateKeeper, targetDirName)
            apiMethod.execute()

            ideStateKeeper.saveReversibleApiCall(apiMethod)
            logger.info("Save 'Change directory' api call to the api calls stack")

            val serverAnswer = ServerAnswer(apiMethod.getExecutionRes())
            call.respondText(gson.toJson(serverAnswer))
            logger.info("Server GET cd result request for dir '$targetDirName' is processed")
        }

        get("/revert-api-calls/{apiCallsCount?}") {
            val apiCallsCount = call.parameters["apiCallsCount"] ?: 1
            if (apiCallsCount !is Int || apiCallsCount.toInt() <= 0) return@get call.respondText(
                text = IdeServerConstants.INCORRECT_API_CALLS_CNT,
                status = HttpStatusCode.BadRequest
            )
            logger.info("Server GET revert $apiCallsCount api calls request is called")

            val revertedApiCallsCount = ideStateKeeper.undoApiCalls(apiCallsCount)
            val serverAnswer = ServerAnswer("Last $revertedApiCallsCount api calls were reverted")
            call.respondText(gson.toJson(serverAnswer))
            logger.info("Server GET revert $apiCallsCount api calls request is processed")
        }

        post("/post-final-ans") {
            logger.info("Server POST final model ans request is called")
            val modelFinalAns = call.receiveText()

            val apiMethod = SaveModelFinalAns(modelFinalAns)
            apiMethod.execute()
            logger.info("Save 'Save model final ans' api call to the api calls stack")

            ideStateKeeper.saveReversibleApiCall(apiMethod)

            val serverAnswer = ServerAnswer(apiMethod.getExecutionRes())
            call.respondText(gson.toJson(serverAnswer))
            logger.info("Server POST final model ans request is processed")
        }
    }
}

object IdeServerConstants {
    const val NO_API_AVAILABLE = "No IDE API available"
    const val ROOT_PAGE_TEXT = "IDE server"
    const val MISSING_FILENAME = "Missing file name"
    const val INCORRECT_API_CALLS_CNT = "apiCallsCount parameter should be a positive integer"
}

@Service(Service.Level.PROJECT)
class IdeServerService(private val project: Project) {
    private lateinit var ideServer: IdeServer

    fun startServer(host: String, port: Int) {
        object : Task.Backgroundable(project, "IDE server start") {
            override fun run(indicator: ProgressIndicator) {
                ideServer = IdeServer(host, port)
                ideServer.startServer(project)
            }
        }.queue()
    }
}