package org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated

import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiModelsTest
import org.jetbrains.research.ideFormerPlugin.api.models.utils.DEFAULT_DIRECTORY_NAME
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper

class ListDirectoryContentsTest : IdeApiModelsTest() {
    fun testListDirectoryContents() {
        val ideStateKeeper = IdeStateKeeper(project)

        mapOf(
            DEFAULT_DIRECTORY_NAME to setOf("dir1", "dir2"),
            "dir1" to setOf("someKtFile2.kt", "pyFile.py", "SomeJavaClass.java", "subdir"),
            "dir2/subdir/subsubdir" to setOf("someTextFile.txt")
        ).forEach { (directoryName, expectedDirectoryItemsNames) ->
            checkListDirectoryContentsExecution(ideStateKeeper, directoryName, expectedDirectoryItemsNames)
        }
        // TODO: to add test for a non-existing dir
    }
}