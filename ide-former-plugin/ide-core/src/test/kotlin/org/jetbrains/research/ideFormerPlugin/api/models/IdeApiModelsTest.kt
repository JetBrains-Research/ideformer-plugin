package org.jetbrains.research.ideFormerPlugin.api.models

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.FileText
import org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated.ChangeDirectory
import org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated.ListDirectoryContents
import org.jetbrains.research.ideFormerPlugin.api.models.utils.DEFAULT_DIRECTORY_NAME
import org.jetbrains.research.ideFormerPlugin.api.models.utils.chooseFileClassesApiForFile
import org.jetbrains.research.ideFormerPlugin.api.models.utils.chooseFileFunctionsApiForFile
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper

// TODO: split the test file into several ones

@TestDataPath("/testData/testProject")
class IdeApiModelsTest : BasePlatformTestCase() {
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
        myFixture.copyFileToProject("dir1/pyFile.py")
        myFixture.copyFileToProject("dir1/SomeJavaClass.java")
        myFixture.copyFileToProject("dir2/subdir/subsubdir/someTextFile.txt")
    }

    private fun checkListDirectoryContentsExecution(
        ideStateKeeper: IdeStateKeeper,
        directoryName: String,
        expectedDirectoryItemsNames: Set<String>
    ) {
        ListDirectoryContents(ideStateKeeper.currentProjectDirectory, directoryName).also {
            it.execute()
            assertEquals(
                expectedDirectoryItemsNames,
                it.getSearchDirectoryItemsNames()?.toSet() ?: error("Search directory items list is null")
            )
        }
    }

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

    private fun checkChangeDirectoryExecution(
        ideStateKeeper: IdeStateKeeper,
        targetDirectoryName: String,
        expectedProjectDirectoryName: String = targetDirectoryName
    ) = ChangeDirectory(ideStateKeeper, targetDirectoryName).also {
        it.execute()
        assertEquals(expectedProjectDirectoryName, ideStateKeeper.currentProjectDirectory.name)
    }

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

    private fun checkFileMethodsExecution(
        ideStateKeeper: IdeStateKeeper,
        fileName: String,
        expectedKtMethodsNames: Set<String>
    ) {
        val fileFunctions = chooseFileFunctionsApiForFile(fileName, ideStateKeeper.currentProjectDirectory)
        fileFunctions.also {
            it.execute()
            assertEquals(
                expectedKtMethodsNames,
                it.getFunctionsNames()?.toSet() ?: error("File functions names list is null")
            )
        }
    }

    fun testFileFunctions() {
        val ideStateKeeper = IdeStateKeeper(project)

        checkChangeDirectoryExecution(ideStateKeeper, "dir1")

        checkFileMethodsExecution(
            ideStateKeeper,
            "someKtFile2.kt",
            setOf("decreaseNum", "printSomePhrase", "delegatePrinting", "SimpleClass", "ComplexClass")
        )
        checkFileMethodsExecution(
            ideStateKeeper,
            "SomeJavaClass.java",
            setOf("setA")
        )
        checkFileMethodsExecution(
            ideStateKeeper,
            "pyFile.py",
            setOf("a_b", "__init__", "bark")
        )

        checkChangeDirectoryExecution(ideStateKeeper, "subdir")

        checkFileMethodsExecution(
            ideStateKeeper,
            "someKtFile1.kt",
            setOf("main", "increaseNum")
        )
    }

    private fun checkFileClassesExecution(
        ideStateKeeper: IdeStateKeeper,
        fileName: String,
        expectedClassesNames: Set<String>
    ) {
        val fileClasses = chooseFileClassesApiForFile(fileName, ideStateKeeper.currentProjectDirectory)
        fileClasses.also {
            it.execute()
            assertEquals(
                expectedClassesNames,
                it.getClassesNames()?.toSet() ?: error("File classes names list is null")
            )
        }
    }

    fun testFileClasses() {
        val ideStateKeeper = IdeStateKeeper(project)

        checkChangeDirectoryExecution(
            ideStateKeeper,
            "dir1",
            "dir1"
        )

        checkFileClassesExecution(
            ideStateKeeper,
            "someKtFile2.kt",
            setOf("SimpleClass", "ComplexClass")
        )
        checkFileClassesExecution(
            ideStateKeeper,
            "pyFile.py",
            setOf("Dog")
        )
        checkFileClassesExecution(
            ideStateKeeper,
            "SomeJavaClass.java",
            setOf("SomeJavaClass")
        )

        // TODO: add tests for getClassCode method
    }

    private fun checkFileTextExecution(
        ideStateKeeper: IdeStateKeeper,
        fileName: String,
        expectedFileText: String
    ) {
        FileText(ideStateKeeper.currentProjectDirectory, fileName).also {
            it.execute()
            assertEquals(expectedFileText, it.getFileText())
        }
    }

    fun testFileText() {
        val ideStateKeeper = IdeStateKeeper(project)

        val cd = checkChangeDirectoryExecution(ideStateKeeper, "dir2/subdir/subsubdir", "subsubdir")
        checkFileTextExecution(
            ideStateKeeper,
            "someTextFile.txt",
            """
                some awesome text
                some another awesome text
                
                happy end!
            """.trimIndent()
        )
        cd.reverse()

        checkChangeDirectoryExecution(
            ideStateKeeper,
            "dir1/subdir",
            "subdir"
        )
        checkFileTextExecution(
            ideStateKeeper,
            "someKtFile1.kt",
            """
                fun main() {
                    println("Hello Kotlin!")
                }

                val num = 0
                fun increaseNum(): Int = return ++num
            """.trimIndent()
        )
    }
}