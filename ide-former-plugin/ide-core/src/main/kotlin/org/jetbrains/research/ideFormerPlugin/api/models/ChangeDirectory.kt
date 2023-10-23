package org.jetbrains.research.ideFormerPlugin.api.models

import com.intellij.psi.PsiDirectory
import org.jetbrains.research.ideFormerPlugin.api.DEFAULT_DIRECTORY_NAME
import org.jetbrains.research.ideFormerPlugin.api.findSubdirectoryRecursively
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper

class ChangeDirectory(
    private val ideStateKeeper: IdeStateKeeper,
    private val targetDirName: String = DEFAULT_DIRECTORY_NAME
) : ReversibleApiMethod {
    private var prevDir: PsiDirectory? = null

    override fun execute() {
        if (targetDirName == DEFAULT_DIRECTORY_NAME) {
            return
        }

        val targetDir = ideStateKeeper.currentProjectDirectory.findSubdirectoryRecursively(targetDirName)
            ?: error("No such directory in a project: '$targetDirName'.")

        prevDir = ideStateKeeper.currentProjectDirectory
        ideStateKeeper.currentProjectDirectory = targetDir
    }

    override fun executionResult(): String =
        when (targetDirName) {
            DEFAULT_DIRECTORY_NAME -> "Working directory remains the same."
            else -> "Working directory was changed to '$targetDirName'."
        }

    override fun reverse() {
        prevDir?.let {
            ideStateKeeper.currentProjectDirectory = it
            prevDir = null
        }
    }
}