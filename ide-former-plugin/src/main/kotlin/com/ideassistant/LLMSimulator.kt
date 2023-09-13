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

    private val lsCdScenarioQueries: List<IDEApiMethod?> = listOf(
        ListDirectoryContents(),
        ChangeDirectory("src"),
        ListDirectoryContents(),
        ChangeDirectory("main"),
        ListDirectoryContents(),
        ChangeDirectory("kotlin"),
        ChangeDirectory("someNotExistingDir"),
        null
    )

    private val lsCdScenarioGenerator = ScenarioGenerator(lsCdScenarioQueries)

    fun getAPIQuery(): IDEApiMethod? = lsCdScenarioGenerator.generateNextQuery()
}