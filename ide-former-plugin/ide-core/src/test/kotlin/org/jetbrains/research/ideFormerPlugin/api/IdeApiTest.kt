package org.jetbrains.research.ideFormerPlugin.api

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper

@TestDataPath("/testData/testProject")
class IdeApiTest : BasePlatformTestCase() {
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
        val ideStateKeeper = IdeStateKeeper(project)

        mapOf(
            DEFAULT_DIRECTORY_NAME to setOf("dir1", "dir2"),
            "dir1" to setOf("someKtFile2.kt",  "subdir"),
            "dir2/subdir/subsubdir" to setOf("someTextFile.txt")
        ).forEach { (directoryName, expectedResult) ->
            ListDirectoryContents(ideStateKeeper.currentProjectDirectory, directoryName).also {
                it.execute()
                assertEquals(expectedResult, it.getDirContentsNames().toSet())
            }
        }
        // TODO: to add test for a non-existing dir
    }

    fun testChangeDirectory() {
        val ideStateKeeper = IdeStateKeeper(project)

        val cdSubDir = ChangeDirectory(ideStateKeeper, "dir1")
        cdSubDir.execute()
        assertEquals("dir1", ideStateKeeper.currentProjectDirectory.name)

        val cdSecondChanging = ChangeDirectory(ideStateKeeper, "subdir")
        cdSecondChanging.execute()
        assertEquals("subdir", ideStateKeeper.currentProjectDirectory.name)

        cdSecondChanging.reverse()
        assertEquals("dir1", ideStateKeeper.currentProjectDirectory.name)

        // second reverse does nothing
        cdSecondChanging.reverse()
        assertEquals("dir1", ideStateKeeper.currentProjectDirectory.name)

        cdSubDir.reverse()
        assertEquals("src", ideStateKeeper.currentProjectDirectory.name)

        val cdDirSeveralLevelsBelow = ChangeDirectory(ideStateKeeper, "dir2/subdir/subsubdir")
        cdDirSeveralLevelsBelow.execute()
        assertEquals("subsubdir", ideStateKeeper.currentProjectDirectory.name)

        // TODO: to add test for a non-existing dir
    }

    fun testGetKtFileKtMethods() {
        val ideStateKeeper = IdeStateKeeper(project)

        var cd = ChangeDirectory(ideStateKeeper, "dir1")
        cd.execute()

        var ktFileMethods = KtFileKtMethods(ideStateKeeper.currentProjectDirectory, "someKtFile2.kt")
        ktFileMethods.execute()
        assertEquals(setOf("decreaseNum", "printSomePhrase"), ktFileMethods.getMethodsNames().toSet())

        cd = ChangeDirectory(ideStateKeeper, "subdir")
        cd.execute()

        ktFileMethods = KtFileKtMethods(ideStateKeeper.currentProjectDirectory, "someKtFile1.kt")
        ktFileMethods.execute()
        assertEquals(setOf("main", "increaseNum"), ktFileMethods.getMethodsNames().toSet())
    }
}