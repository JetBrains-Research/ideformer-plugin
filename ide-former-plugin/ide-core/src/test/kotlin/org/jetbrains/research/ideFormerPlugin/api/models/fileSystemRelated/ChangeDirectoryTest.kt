package org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated

import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiModelsTest
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper

class ChangeDirectoryTest : IdeApiModelsTest() {
    fun testChangeDirectory() {
        val ideStateKeeper = IdeStateKeeper(project)

        val cdSubDir = checkChangeDirectoryExecution(ideStateKeeper, "dir1")
        val cdSecondChanging = checkChangeDirectoryExecution(ideStateKeeper, "subdir")

        cdSecondChanging.reverse()
        assertEquals("dir1", ideStateKeeper.currentProjectDirectory.name)

        // second reverse does nothing
        cdSecondChanging.reverse()
        assertEquals("dir1", ideStateKeeper.currentProjectDirectory.name)

        cdSubDir.reverse()
        assertEquals("src", ideStateKeeper.currentProjectDirectory.name)

        checkChangeDirectoryExecution(ideStateKeeper, "dir2/subdir/subsubdir", "subsubdir")
        // TODO: to add test for a non-existing dir
    }
}