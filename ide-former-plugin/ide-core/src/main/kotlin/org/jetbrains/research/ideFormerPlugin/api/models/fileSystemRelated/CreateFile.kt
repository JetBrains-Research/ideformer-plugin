package org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated

import com.intellij.psi.PsiDirectory
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod
import org.jetbrains.research.ideFormerPlugin.api.models.utils.createFileByName
import org.jetbrains.research.ideFormerPlugin.api.models.utils.deleteFileByName

class CreateFile(
    private val projectDirectory: PsiDirectory,
    private val fileName: String
) : ReversibleApiMethod {

    override fun execute() {
        // TODO: to fix: if a file is deleted manually from the project, it's still has status 'exist' in the project directory
        // TODO: common problem. projectDirectory isn't updated when manually actions are done.
        projectDirectory.createFileByName(fileName)
    }

    override fun reverse() {
        projectDirectory.deleteFileByName(fileName)
    }
}