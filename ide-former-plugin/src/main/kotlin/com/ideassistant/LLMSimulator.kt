package com.ideassistant

import com.intellij.openapi.project.Project

class LLMSimulator(userProject: Project) {
    private class KtMethodsScenarioGenerator(userProject: Project) {
        private var queryNum = 0

        private val queryList: List<IDEApiMethod?> = listOf(
            GetAllProjectModules(userProject),
            GetAllModuleFiles(userProject, "main"),
            GetAllKtFileKtMethods(userProject, "Main.kt"),
            null
        )

        fun generateNextQuery(): IDEApiMethod? {
            val nextQuery = queryList[queryNum++]
            queryNum %= queryList.size
            return nextQuery
        }
    }

    private val scenarioGenerator = KtMethodsScenarioGenerator(userProject)

    fun getAPIQuery(): IDEApiMethod? = scenarioGenerator.generateNextQuery()
}