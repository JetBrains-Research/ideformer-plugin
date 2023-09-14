package com.ideassistant

import com.intellij.openapi.project.Project

class LLMSimulator(userProject: Project) {
    private val ideStateKeeper: IDEStateKeeper = IDEStateKeeper(userProject)

    private class ScenarioGenerator(private val queryList: List<IDEApiMethod?>) {
        private var queryNum = 0

        fun generateNextQuery(): IDEApiMethod? {
            val nextQuery = queryList[queryNum++]
            queryNum %= queryList.size
            return nextQuery
        }
    }

    private val lsCdScenarioQueries: List<IDEApiMethod?> = listOf(
        ListDirectoryContents(ideStateKeeper.curDirectory),
        ChangeDirectory(ideStateKeeper, "src"),
        ListDirectoryContents(ideStateKeeper.curDirectory),
        ChangeDirectory(ideStateKeeper, "main"),
        ListDirectoryContents(ideStateKeeper.curDirectory),
        ChangeDirectory(ideStateKeeper, "kotlin"),
        ChangeDirectory(ideStateKeeper, "someNotExistingDir"),
        null
    )

    private val lsCdScenarioGenerator = ScenarioGenerator(lsCdScenarioQueries)

    fun getAPIQuery(): IDEApiMethod? = lsCdScenarioGenerator.generateNextQuery()
}