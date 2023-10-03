package org.jetbrains.research.ideFormerPlugin.modelSimulator

import com.intellij.openapi.project.Project
import org.jetbrains.research.ideFormerPlugin.api.ChangeDirectory
import org.jetbrains.research.ideFormerPlugin.api.IdeApiMethod
import org.jetbrains.research.ideFormerPlugin.api.ListDirectoryContents
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper

class LLMSimulator(userProject: Project) {
    private val ideStateKeeper: IdeStateKeeper = IdeStateKeeper(userProject)

    private class ScenarioGenerator(private val queryList: List<IdeApiMethod?>) {
        private var queryNum = 0

        fun generateNextQuery(): IdeApiMethod? {
            val nextQuery = queryList[queryNum++]
            queryNum %= queryList.size
            return nextQuery
        }
    }

    private val lsCdScenarioQueries: List<IdeApiMethod?> = listOf(
        ListDirectoryContents(ideStateKeeper.curDirectory),
        ChangeDirectory(ideStateKeeper, "src"),
        ListDirectoryContents(ideStateKeeper.curDirectory),
        ChangeDirectory(ideStateKeeper, "main"),
        ListDirectoryContents(ideStateKeeper.curDirectory),
        ChangeDirectory(ideStateKeeper, "kotlin"),
        // ChangeDirectory(ideStateKeeper, "someNotExistingDir"),
        null
    )

    private val lsCdScenarioGenerator = ScenarioGenerator(lsCdScenarioQueries)

    fun getAPIQuery(): IdeApiMethod? = lsCdScenarioGenerator.generateNextQuery()
}