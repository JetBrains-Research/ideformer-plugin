package org.jetbrains.research.ideFormerPlugin.server

import com.google.gson.Gson
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.server.requests.*
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Application.configureRouting(ideStateKeeper: IdeStateKeeper, logger: Logger) {
    install(IgnoreTrailingSlash)

    routing {
        getMainPage(logger)
        getIdeApiList(logger)
        getProjectModules(logger, ideStateKeeper)
        getKtFileKtMethods(logger, ideStateKeeper)
        getListDirectoryContents(logger, ideStateKeeper)
        getChangeDirectory(logger, ideStateKeeper)
        getReverseApiMethods(logger, ideStateKeeper)
        postFinalAnswer(logger, ideStateKeeper)
    }
}

val jsonConverter = Gson()

object IdeServerConstants {
    const val NO_API_AVAILABLE = "No IDE API available"
    const val ROOT_PAGE_TEXT = "IDE server"
    const val MISSING_FILENAME = "Missing file name"
    const val NEGATIVE_API_CALLS_CNT = "apiCallsCount parameter should be a positive integer"
    const val NOT_A_NUMBER_API_CALLS_CNT = "apiCallsCount parameter should be a number"
    const val DEFAULT_API_CALLS_CNT = "1"
    const val API_EXECUTION_UNKNOWN_ERROR = "Unknown error while api method execution"
}