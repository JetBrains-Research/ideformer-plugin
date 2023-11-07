package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated

import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiModelsTest
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper

class FileTextTest : IdeApiModelsTest() {
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