package org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDirectory
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod
import org.jetbrains.research.ideFormerPlugin.api.models.utils.findSubdirectoryRecursively
import org.jetbrains.research.ideFormerPlugin.api.models.utils.refresh

class CreateDirectory(
    private val projectDirectory: PsiDirectory,
    private val directoryName: String
) : ReversibleApiMethod {

    override fun execute() {
        WriteCommandAction.runWriteCommandAction(projectDirectory.project) {
            projectDirectory.refresh()
            projectDirectory.createSubdirectory(directoryName)
        }
    }

    override fun reverse() {
        WriteCommandAction.runWriteCommandAction(projectDirectory.project) {
            projectDirectory.refresh()
            val psiDirectory = projectDirectory.findSubdirectoryRecursively(directoryName)
            psiDirectory.delete()
        }
    }
}