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

        // TODO: create parametrized tests

        val ktFileClasses = checkFileClassesExecution(
            ideStateKeeper,
            "someKtFile2.kt",
            setOf("SimpleClass", "ComplexClass")
        )
        checkFileClassCodeGetting(
            ktFileClasses,
            "SimpleClass",
            """
                class SimpleClass(private val num: Int = 0) {
                    fun decreaseNum(): Int = return ++num

                    fun printSomePhrase(phrase: String = "some phrase") = println(phrase)
                }
            """.trimIndent()
        )

        val pyFileClasses = checkFileClassesExecution(
            ideStateKeeper,
            "pyFile.py",
            setOf("Dog")
        )
        checkFileClassCodeGetting(
            pyFileClasses,
            "Dog",
            """
                class Dog:
                    def __init__(self, name, age):
                        self.name = name
                        self.age = age

                    def bark(self):
                        print("bark bark!")
            """.trimIndent()
        )

        val javaFileClasses = checkFileClassesExecution(
            ideStateKeeper,
            "SomeJavaClass.java",
            setOf("SomeJavaClass")
        )
        checkFileClassCodeGetting(
            javaFileClasses,
            "SomeJavaClass",
            """
                class SomeJavaClass() {
                    private Integer a = 1;

                    public void setA(Integer a) {
                        this.a = a;
                    }
                }
            """.trimIndent()
        )
    }
}