package org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated

import com.intellij.psi.PsiDirectory
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod
import org.jetbrains.research.ideFormerPlugin.api.models.utils.*
import org.jetbrains.research.ideFormerPlugin.stateKeeper.IdeStateKeeper

class ChangeDirectory(
    private val ideStateKeeper: IdeStateKeeper,
    private val targetDirectoryName: String = DEFAULT_DIRECTORY_NAME
) : ReversibleApiMethod {
    private var previousDirectory: PsiDirectory? = null

    override fun execute() {
        val currentProjectDirectory = ideStateKeeper.currentProjectDirectory
        currentProjectDirectory.refresh()

        val targetDir = currentProjectDirectory.findDirectoryRecursively(targetDirectoryName)

        previousDirectory = currentProjectDirectory
        ideStateKeeper.currentProjectDirectory = targetDir
    }

    override fun reverse() {
        if (previousDirectory == null) error(UNCALLED_EXECUTE_BEFORE_RESULT_GETTING)

        previousDirectory!!.let {
            ideStateKeeper.currentProjectDirectory = it
            previousDirectory = null
        }
    }
}