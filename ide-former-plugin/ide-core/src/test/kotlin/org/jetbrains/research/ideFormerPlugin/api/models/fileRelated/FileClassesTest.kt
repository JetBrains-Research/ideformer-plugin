package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated

import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiModelsTest
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper

class FileClassesTest : IdeApiModelsTest() {
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
}