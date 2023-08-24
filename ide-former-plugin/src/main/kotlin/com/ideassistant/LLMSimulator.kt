package com.ideassistant

import com.intellij.openapi.project.Project

object LLMSimulator {
    private const val API_CALL_INFO = "[API call info]:"
    private const val API_CALL_RES = "[API call res]:"

    fun processUserQuery(userQuery: String, userProject: Project): String {
        val modelActionChain = StringBuilder()

        var prevStepInfo = userQuery
        while (true) {
            val modelAPIMethodQuery: IDEApiMethod = generateAPIQuery(prevStepInfo, userProject) ?: break
            modelActionChain.append("$API_CALL_INFO $modelAPIMethodQuery")
            prevStepInfo = modelAPIMethodQuery.call()
            modelActionChain.append("$API_CALL_RES $prevStepInfo\n")
        }

        return modelActionChain.toString()
    }

    private object APIQueryGenerator {
        const val FINISH_QUERY_CHAIN = "Finish query chain"
        private var queryNum = 0
        // TODO: fill in the list with the correct API sequence
        private val queryList = listOf("GetAllProjectFiles", FINISH_QUERY_CHAIN)

        fun generateNextQuery(): String {
            val nextQuery = queryList[queryNum++]
            queryNum %= queryList.size
            return nextQuery
        }
    }

    private fun generateAPIQuery(prevStepInfo: String, userProject: Project): IDEApiMethod? {
        val apiMethodName = APIQueryGenerator.generateNextQuery()

        return when (apiMethodName) {
            // TODO: sync with future new APIs, add args generating and instance of IDEApiMethod creating
            "GetAllProjectModules" -> {return null}
            "GetAllProjectFiles" -> {return null}
            "GetAllModuleFiles" -> {return null}
            "GetAllProjectKtMethods" -> {return null}
            "GetProjectFileByName" -> {return null}
            APIQueryGenerator.FINISH_QUERY_CHAIN -> null
            else -> throw IllegalArgumentException("Illegal API method")
        }
    }
}