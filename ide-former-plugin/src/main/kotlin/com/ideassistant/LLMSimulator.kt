package com.ideassistant

object LLMSimulator {
    private object APIQueryGenerator {
        const val FINISH_QUERY_CHAIN = "Finish query chain"
        private var queryNum = 0
        // TODO: fill in the list with the correct API sequence
        private val queryList = listOf("GetAllProjectModules", FINISH_QUERY_CHAIN)

        fun generateNextQuery(): String {
            val nextQuery = queryList[queryNum++]
            queryNum %= queryList.size
            return nextQuery
        }
    }

    fun getAPIQuery(prevStepInfo: String): IDEApiMethod? {
        val apiMethodName = APIQueryGenerator.generateNextQuery()

        return when (apiMethodName) {
            // TODO: sync with future new APIs, add args generating and instance of IDEApiMethod creating
            "GetAllProjectModules" -> {
                GetAllProjectModules
            }
            "GetAllModuleFiles" -> {return null}
            "GetAllProjectKtMethods" -> {return null}
            "GetProjectFileByName" -> {return null}
            APIQueryGenerator.FINISH_QUERY_CHAIN -> null
            else -> throw IllegalArgumentException("Illegal API method")
        }
    }
}