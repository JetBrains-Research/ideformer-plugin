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
    }
}