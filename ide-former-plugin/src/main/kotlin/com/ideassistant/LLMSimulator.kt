package com.ideassistant

object LLMSimulator {
    private object KtMethodsScenarioGenerator {
        private var queryNum = 0

        private val queryList = listOf(
            GetAllProjectModules,
            GetAllModuleFiles("main"),
            GetAllKtFileKtMethods("Main.kt"),
            null
        )

        fun generateNextQuery(): IDEApiMethod? {
            val nextQuery = queryList[queryNum++]
            queryNum %= queryList.size
            return nextQuery
        }
    }

    // TODO: when a model appears here instead of a temporary scenario, it will need the prev step information
    fun getAPIQuery(prevStepInfo: String): IDEApiMethod? = KtMethodsScenarioGenerator.generateNextQuery()
}