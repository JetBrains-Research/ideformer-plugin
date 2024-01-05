package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated

import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiModelsTest
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper

class FileFunctionsTest : IdeApiModelsTest() {
    fun testFileFunctions() {
        val ideStateKeeper = IdeStateKeeper(project)

        checkChangeDirectoryExecution(ideStateKeeper, "dir1")

        // TODO: create parametrized tests

         val ktFileMethods = checkFileFunctionsExecution(
            ideStateKeeper,
            "someKtFile2.kt",
            setOf("decreaseNum", "printSomePhrase", "delegatePrinting", "SimpleClass", "ComplexClass")
        )
        checkFileFunctionCodeGetting(
            ktFileMethods,
            "printSomePhrase",
            """
                fun printSomePhrase(phrase: String = "some phrase") = println(phrase)
            """.trimIndent()
        )


        val javaFileMethods = checkFileFunctionsExecution(
            ideStateKeeper,
            "SomeJavaClass.java",
            setOf("setA")
        )
        checkFileFunctionCodeGetting(
            javaFileMethods,
            "setA",
            """
               public void setA(Integer a) {
                       this.a = a;
                   }
            """.trimIndent()
        )

        val pyFileMethods = checkFileFunctionsExecution(
            ideStateKeeper,
            "pyFile.py",
            setOf("a_b", "__init__", "bark")
        )
        // function outside the class
        checkFileFunctionCodeGetting(
            pyFileMethods,
            "a_b",
            """
               def a_b(a: int, b: int) -> int:
                   return a + b
            """.trimIndent()
        )
        // function inside the class
        checkFileFunctionCodeGetting(
            pyFileMethods,
            "__init__",
            """
               def __init__(self, name, age):
                       self.name = name
                       self.age = age
            """.trimIndent()
        )

        // check file functions for a file with the complex structured path
        checkFileFunctionsExecution(
            ideStateKeeper,
            "subdir/someKtFile1.kt",
            setOf("main", "increaseNum")
        )
    }
}