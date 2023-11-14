package org.jetbrains.research.ideFormerPlugin.api.models

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.FileText
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

    protected fun checkFileMethodsExecution(
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
        FileText(ideStateKeeper.currentProjectDirectory, fileName).also {
            it.execute()
            assertEquals(expectedFileText, it.getFileText())
        }
    }
}