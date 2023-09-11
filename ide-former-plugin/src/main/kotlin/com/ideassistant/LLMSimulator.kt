package com.ideassistant

class LLMSimulator {
    private class ScenarioGenerator(private val queryList: List<IDEApiMethod?>) {
        private var queryNum = 0

        fun generateNextQuery(): IDEApiMethod? {
            val nextQuery = queryList[queryNum++]
            queryNum %= queryList.size
            return nextQuery
        }
    }

    private val ktMethodsScenarioQueries: List<IDEApiMethod?> = listOf(
        GetAllProjectModules(),
        GetAllModuleFiles("main"),
        GetAllKtFileKtMethods("Main.kt"),
        null
    )

    private val lsCdScenarioQueries: List<IDEApiMethod?> = listOf(
        ListDirectories(),
        ChangeDirectory("src"),
        ListDirectories(),
        ChangeDirectory("main"),
        ListDirectories(),
        ChangeDirectory("kotlin"),
        null
    )

    private val lsCdScenariosScenarioGenerator = ScenarioGenerator(lsCdScenarioQueries)

    fun getAPIQuery(): IDEApiMethod? = lsCdScenariosScenarioGenerator.generateNextQuery()
}