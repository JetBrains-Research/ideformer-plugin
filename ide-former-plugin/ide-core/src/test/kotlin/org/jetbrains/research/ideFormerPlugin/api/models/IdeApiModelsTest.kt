package org.jetbrains.research.ideFormerPlugin.api.models

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileText.GetFileText
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses.FileClasses
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions.FileFunctions
import org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated.ChangeDirectory
import org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated.ListDirectoryContents
import org.jetbrains.research.ideFormerPlugin.api.models.utils.chooseFileClassesApiForFile
import org.jetbrains.research.ideFormerPlugin.api.models.utils.chooseFileFunctionsApiForFile
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper

@TestDataPath("/testData/testProject")
abstract class IdeApiModelsTest : BasePlatformTestCase() {
    override fun getTestDataPath(): String = "src/test/testData/testProject"

    override fun setUp() {
        super.setUp()
        configureTestProject()
    }

    private fun configureTestProject() {
        myFixture.copyFileToProject("dir1/subdir/someKtFile1.kt")
        myFixture.copyFileToProject("dir1/someKtFile2.kt")
        myFixture.copyFileToProject("dir1/pyFile.py")
        myFixture.copyFileToProject("dir1/SomeJavaClass.java")
        myFixture.copyFileToProject("dir2/subdir/subsubdir/someTextFile.txt")

        // git repo init
//        val git = Git.getInstance()
//        val commandResult = git.init(project, project.projectFile!!)
//        if (!commandResult.success()) {
//            println("Git was successfully initialized for this project")
//        } else {
//            error("Error while git repo init")
//        }
    }

    protected fun checkListDirectoryContentsExecution(
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

    protected fun checkChangeDirectoryExecution(
        ideStateKeeper: IdeStateKeeper,
        targetDirectoryName: String,
        expectedProjectDirectoryName: String = targetDirectoryName
    ) = ChangeDirectory(ideStateKeeper, targetDirectoryName).also {
        it.execute()
        assertEquals(expectedProjectDirectoryName, ideStateKeeper.currentProjectDirectory.name)
    }

    protected fun checkFileFunctionsExecution(
        ideStateKeeper: IdeStateKeeper,
        fileName: String,
        expectedKtMethodsNames: Set<String>
    ): FileFunctions {
        val fileFunctions = chooseFileFunctionsApiForFile(fileName, ideStateKeeper.currentProjectDirectory)
        fileFunctions.also {
            it.execute()
            assertEquals(
                expectedKtMethodsNames,
                it.getFunctionsNames()?.toSet() ?: error("File functions names list is null")
            )
        }
        return fileFunctions
    }

    /** The absence of the necessary indentation on the 1st line before the start of the function is ok,
    bc the function text obtained from PSI begins exactly with the first non-space letter of the function code
     **/
    protected fun checkFileFunctionCodeGetting(
        fileFunctions: FileFunctions,
        functionName: String,
        expectedMethodCode: String
    ) {
        fileFunctions.getFunctionCode(functionName)?.also {
            assertEquals(expectedMethodCode, it)
        }
    }

    protected fun checkFileClassesExecution(
        ideStateKeeper: IdeStateKeeper,
        fileName: String,
        expectedClassesNames: Set<String>
    ): FileClasses {
        val fileClasses = chooseFileClassesApiForFile(fileName, ideStateKeeper.currentProjectDirectory)
        fileClasses.also {
            it.execute()
            assertEquals(
                expectedClassesNames,
                it.getClassesNames()?.toSet() ?: error("File classes names list is null")
            )
        }
        return fileClasses
    }

    protected fun checkFileClassCodeGetting(
        fileClasses: FileClasses,
        className: String,
        expectedClassCode: String
    ) {
        fileClasses.getClassCode(className)?.also {
            assertEquals(expectedClassCode, it)
        }
    }

    protected fun checkFileTextExecution(
        ideStateKeeper: IdeStateKeeper,
        fileName: String,
        expectedFileText: String
    ) {
        GetFileText(ideStateKeeper.currentProjectDirectory, fileName).also {
            it.execute()
            assertEquals(expectedFileText, it.getFileText())
        }
    }
}