package com.ideassistant

class LLMSimulator {
    private class KtMethodsScenarioGenerator {
        private var queryNum = 0

        private val queryList: List<IDEApiMethod?> = listOf(
            GetAllProjectModules(),
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

    private val scenarioGenerator = KtMethodsScenarioGenerator()

    fun getAPIQuery(): IDEApiMethod? = scenarioGenerator.generateNextQuery()
}