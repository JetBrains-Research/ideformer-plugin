package com.ideassistant

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase

@TestDataPath("\$CONTENT_ROOT/testData/testProject")
class IDEApiTest : BasePlatformTestCase() {
    override fun getTestDataPath(): String = "src/test/testData/testProject"

    override fun setUp() {
        super.setUp()
        configureTestProject()
    }


    /*
    Test project structure:

    testProject
              |-- dir1
                     |-- subdir
                     |       |-- someKtFile1.kt
                     |-- someKtFile2.kt
              |-- dir2
                     |-- subdir
                              |-- subsubdir
                                         |-- someTextFile.txt
     */

    private fun configureTestProject() {
        myFixture.copyFileToProject("dir1/subdir/someKtFile1.kt")
        myFixture.copyFileToProject("dir1/someKtFile2.kt")
        myFixture.copyFileToProject("dir2/subdir/subsubdir/someTextFile.txt")
    }

    fun testListDirectoryContents() {
        val ideStateKeeper = IDEStateKeeper(project)

        val lsCurDir = ListDirectoryContents(ideStateKeeper.curDirectory)
        lsCurDir.execute()
        assertEquals(setOf("dir1", "dir2"), lsCurDir.getDirContentsNames().toSet())

        val lsSubDir = ListDirectoryContents(ideStateKeeper.curDirectory, "dir1")
        lsSubDir.execute()
        assertEquals(setOf("someKtFile2.kt",  "subdir"), lsSubDir.getDirContentsNames().toSet())

        val lsDirSeveralLevelsBelow = ListDirectoryContents(ideStateKeeper.curDirectory, "dir2/subdir/subsubdir")
        lsDirSeveralLevelsBelow.execute()
        assertEquals(setOf("someTextFile.txt"), lsDirSeveralLevelsBelow.getDirContentsNames().toSet())

        // TODO: to add test for a non-existing dir
    }

    fun testChangeDirectory() {
        val ideStateKeeper = IDEStateKeeper(project)

        val cdSubDir = ChangeDirectory(ideStateKeeper, "dir1")
        cdSubDir.execute()
        assertEquals("dir1", ideStateKeeper.curDirectory.name)

        val cdSecondChanging = ChangeDirectory(ideStateKeeper, "subdir")
        cdSecondChanging.execute()
        assertEquals("subdir", ideStateKeeper.curDirectory.name)

        cdSecondChanging.reverse()
        assertEquals("dir1", ideStateKeeper.curDirectory.name)

        // second reverse does nothing
        cdSecondChanging.reverse()
        assertEquals("dir1", ideStateKeeper.curDirectory.name)

        cdSubDir.reverse()
        assertEquals("src", ideStateKeeper.curDirectory.name)

        val cdDirSeveralLevelsBelow = ChangeDirectory(ideStateKeeper, "dir2/subdir/subsubdir")
        cdDirSeveralLevelsBelow.execute()
        assertEquals("subsubdir", ideStateKeeper.curDirectory.name)

        // TODO: to add test for a non-existing dir
    }
}