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
        // TODO: to add this refreshing everywhere
        projectDirectory.virtualFile.refresh(false, false)
        projectDirectory.deleteFileByName(fileName)
    }

    override fun reverse() {
        val psiFile = projectDirectory.createFileByName(fileName)
        psiFile.setText(deletedFileText)
    }
}