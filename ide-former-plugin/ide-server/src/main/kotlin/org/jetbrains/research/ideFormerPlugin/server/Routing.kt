package org.jetbrains.research.ideFormerPlugin.server

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.research.ideFormerPlugin.server.requests.*
import org.jetbrains.research.ideFormerPlugin.server.requests.fileRelated.getFileClasses
import org.jetbrains.research.ideFormerPlugin.server.requests.fileRelated.getFileFunctions
import org.jetbrains.research.ideFormerPlugin.server.requests.fileRelated.getFileText
import org.jetbrains.research.ideFormerPlugin.server.requests.fileSystemRelated.getChangeDirectory
import org.jetbrains.research.ideFormerPlugin.server.requests.fileSystemRelated.getListDirectoryContents
import org.jetbrains.research.ideFormerPlugin.server.requests.fileSystemRelated.getProjectModules
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper
import org.slf4j.Logger

fun Application.configureRouting(ideStateKeeper: IdeStateKeeper, logger: Logger) {
    install(IgnoreTrailingSlash)

    routing {
        getMainPage(logger)
        getIdeApiList(logger)
        getProjectModules(logger, ideStateKeeper)
        getFileText(logger, ideStateKeeper)
        getFileFunctions(logger, ideStateKeeper)
        getFileClasses(logger, ideStateKeeper)
        getListDirectoryContents(logger, ideStateKeeper)
        getChangeDirectory(logger, ideStateKeeper)
        getReverseApiMethods(logger, ideStateKeeper)
        postFinalAnswer(logger, ideStateKeeper)
    }
}

object IdeServerConstants {
    const val NO_API_AVAILABLE = "No IDE API available"
    const val ROOT_PAGE_TEXT = "IDE server"
    const val MISSING_FILENAME = "Missing file name"
    const val PROJECT_DIR_REMAINS_THE_SAME = "Project directory remains the same"
    const val PROJECT_DIR_WAS_CHANGED_TO = "Project directory was successfully changed to"
    const val NEGATIVE_API_METHODS_CNT = "apiMethodsCount parameter should be a positive integer"
    const val NOT_A_NUMBER_API_METHODS_CNT = "apiMethodsCount parameter should be a number"
    const val API_EXECUTION_UNKNOWN_ERROR = "Unknown error while api method execution"
    const val INCORRECT_REQUESTED_FILE_EXTENSION =
        "Incorrect requested file extension: only .py, .kt, .java file can be processed"

    // TODO: renaming
    const val FILENAME_REQUEST_PARAMETER = "fileName"
    const val CLASSNAME_REQUEST_PARAMETER = "className"
    const val FUNCTION_NAME_REQUEST_PARAMETER = "functionName"
}