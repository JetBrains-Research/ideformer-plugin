package org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDirectory
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod
import org.jetbrains.research.ideFormerPlugin.api.models.utils.findFileRecursively

class CreateFile(
    private val projectDirectory: PsiDirectory,
    private val fileName: String
) : ReversibleApiMethod {

    override fun execute() {
        // TODO: to fix: if a file is deleted manually from the project, it's still has status 'exist' in the project directory
        // TODO: common problem. projectDirectory isn't updated when manually actions are done.
        WriteCommandAction.runWriteCommandAction(projectDirectory.project) {
            projectDirectory.createFile(fileName)
        }
    }

    override fun reverse() {
        WriteCommandAction.runWriteCommandAction(projectDirectory.project) {
            projectDirectory.findFileRecursively(fileName).delete()
        }
    }
}