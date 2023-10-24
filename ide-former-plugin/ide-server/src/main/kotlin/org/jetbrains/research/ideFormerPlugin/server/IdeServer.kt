package org.jetbrains.research.ideFormerPlugin.server

import com.google.gson.Gson
import com.intellij.openapi.project.Project
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.api.DEFAULT_DIRECTORY_NAME
import org.jetbrains.research.ideFormerPlugin.api.models.*
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class IdeServer(
    private val host: String = "localhost",
    private val port: Int = 8082
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

fun Application.configureRouting(ideStateKeeper: IdeStateKeeper, logger: Logger) {
    install(IgnoreTrailingSlash)
    val gson = Gson()

    routing {
        get("/") {
            logger.info("Server GET root page request is called")
            call.respondText(IdeServerConstants.ROOT_PAGE_TEXT)
            logger.info("Server GET root page request is processed")
        }

        get("/ide-api-list") {
            logger.info("Server GET ide api list request is called")

            val apiDescriptions =
                IdeServer::class.java.getResource("ideApiDescriptions.json")!!.readText()
            apiDescriptions.ifEmpty { IdeServerConstants.NO_API_AVAILABLE }
            logger.info("Api descriptions were retrieved")

            call.respondText(gson.toJson(apiDescriptions))
            logger.info("Server GET ide api list request is processed")
        }

        get("/project-modules") {
            logger.info("Server GET project modules request is called")

            val projectModules = ProjectModules(ideStateKeeper.userProject)
            projectModules.execute()

            call.respondText(gson.toJson(projectModules.getProjectModulesNames()))
            logger.info("Server GET project modules request is processed")
        }

        get("/file-kt-methods/{fileName}") {
            val fileName = call.parameters["fileName"] ?: return@get call.respondText(
                text = IdeServerConstants.MISSING_FILENAME,
                status = HttpStatusCode.BadRequest
            )
            logger.info("Server GET file kt methods request for file '$fileName' is called")

            val ktFileKtMethods = KtFileKtMethods(ideStateKeeper.currentProjectDirectory, fileName)
            ktFileKtMethods.execute()
            // TODO: if error was caught here, return string with the error explanation

            call.respondText(gson.toJson(ktFileKtMethods.getFileKtMethodsNames()))
            logger.info("Server GET file kt methods request for file '$fileName' is processed")

        }

        get("/list-dir-contents/{dirName?}") {
            val dirName = call.parameters["dirName"] ?: "."
            logger.info("Server GET ls request for dir '$dirName' is called")

            val listDirectoryContents = ListDirectoryContents(ideStateKeeper.currentProjectDirectory, dirName)
            listDirectoryContents.execute()
            // TODO: if error was caught here, return string with the error explanation


            call.respondText(gson.toJson(listDirectoryContents.getSearchDirectoryItemsNames()))
            logger.info("Server GET ls request for dir '$dirName' is processed")
        }

        get("/change-dir/{targetDirName?}") {
            val targetDirName = call.parameters["targetDirName"] ?: "."
            logger.info("Server GET cd result request for dir '$targetDirName' is called")

            val changeDirectory = ChangeDirectory(ideStateKeeper, targetDirName)
            changeDirectory.execute()

            // TODO: if there were exception in method execution, do not save this method and add error desc
            ideStateKeeper.saveReversibleApiMethod(changeDirectory)
            logger.info("Change directory api method was saved on the api calls stack")

            val response = if (targetDirName == DEFAULT_DIRECTORY_NAME) {
                "Project directory remains the same."
            } else {
                "Project directory was successfully changed to $targetDirName."
            }

            call.respondText(gson.toJson(response))
            logger.info("Server GET cd result request for dir '$targetDirName' is processed")
        }

        get("/reverse-api-methods/{apiMethodsCount?}") {
            val apiCallsCountString = call.parameters["apiMethodsCount"] ?: IdeServerConstants.DEFAULT_API_CALLS_CNT

            val apiCallsCount = apiCallsCountString.toIntOrNull() ?: return@get call.respondText(
                text = IdeServerConstants.NOT_A_NUMBER_API_CALLS_CNT,
                status = HttpStatusCode.BadRequest
            )
            if (apiCallsCount <= 0) return@get call.respondText(
                text = IdeServerConstants.NEGATIVE_API_CALLS_CNT,
                status = HttpStatusCode.BadRequest
            )

            logger.info("Server GET reverse $apiCallsCount api methods request is called")

            val revertedApiCallsCount = ideStateKeeper.reverseLastApiMethods(apiCallsCount)
            call.respondText(gson.toJson("Last $revertedApiCallsCount api calls were reverted"))
            logger.info("Server GET reverse $apiCallsCount api methods request is processed")
        }

        post("/post-final-ans") {
            logger.info("Server POST final model answer request is called")
            val modelFinalAns = call.receiveText()

            val apiMethod = SaveModelFinalAns(modelFinalAns)
            apiMethod.execute()
            logger.info("Save model final ans api call was executed")

            ideStateKeeper.saveReversibleApiMethod(apiMethod)
            logger.info("Save model final ans api call was added to the api methods stack")

            call.respondText(gson.toJson("Model ans was successfully saved"))
            logger.info("Server POST final model answer request is processed")
        }
    }
}

object IdeServerConstants {
    const val NO_API_AVAILABLE = "No IDE API available"
    const val ROOT_PAGE_TEXT = "IDE server"
    const val MISSING_FILENAME = "Missing file name"
    const val NEGATIVE_API_CALLS_CNT = "apiCallsCount parameter should be a positive integer"
    const val NOT_A_NUMBER_API_CALLS_CNT = "apiCallsCount parameter should be a number"
    const val DEFAULT_API_CALLS_CNT = "1"
}