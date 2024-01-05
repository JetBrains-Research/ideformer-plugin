package org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated

import com.intellij.psi.PsiDirectory
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod
import org.jetbrains.research.ideFormerPlugin.api.models.utils.*

class DeleteFile(
    private val projectDirectory: PsiDirectory,
    private val fileName: String
) : ReversibleApiMethod {
    private val deletedFileText: String = projectDirectory.findFileRecursively(fileName).text

    override fun execute() {
        // TODO: to fix: if a file is deleted manually from the project, it's still has status 'exist' in the project directory
        // TODO: common problem. projectDirectory isn't updated when manually actions are done.
        projectDirectory.deleteFileByName(fileName)
    }

    override fun reverse() {
        val psiFile = projectDirectory.createFileByName(fileName)
        // TODO: to fix it. a method doesn't work
        psiFile.setText(deletedFileText)
    }
}