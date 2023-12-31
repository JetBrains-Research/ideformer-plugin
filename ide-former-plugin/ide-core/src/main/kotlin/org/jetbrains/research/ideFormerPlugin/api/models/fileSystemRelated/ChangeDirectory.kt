package org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated

import com.intellij.psi.PsiDirectory
import org.jetbrains.research.ideFormerPlugin.api.models.utils.DEFAULT_DIRECTORY_NAME
import org.jetbrains.research.ideFormerPlugin.api.models.utils.findSubdirectoryRecursively
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper

class ChangeDirectory(
    private val ideStateKeeper: IdeStateKeeper,
    private val targetDirectoryName: String = DEFAULT_DIRECTORY_NAME
) : ReversibleApiMethod {
    private var prevDir: PsiDirectory? = null

    override fun execute() {
        val targetDir = ideStateKeeper.currentProjectDirectory.findSubdirectoryRecursively(targetDirectoryName)

        prevDir = ideStateKeeper.currentProjectDirectory
        ideStateKeeper.currentProjectDirectory = targetDir
    }

    override fun reverse() {
        prevDir?.let {
            ideStateKeeper.currentProjectDirectory = it
            prevDir = null
        }
    }
}