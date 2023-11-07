package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated

import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiModelsTest
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper

class FileFunctionsTest : IdeApiModelsTest() {
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
}