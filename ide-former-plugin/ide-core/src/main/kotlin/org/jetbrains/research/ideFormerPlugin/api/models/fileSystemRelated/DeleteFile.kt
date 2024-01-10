package org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated

import com.intellij.psi.PsiDirectory
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod
import org.jetbrains.research.ideFormerPlugin.api.models.utils.*

class DeleteFile(
    private val projectDirectory: PsiDirectory,
    private val fileName: String
) : ReversibleApiMethod {
    private var deletedFileText: String? = projectDirectory.findFileRecursively(fileName).text

    override fun execute() {
        projectDirectory.refresh()
        projectDirectory.deleteFileByName(fileName)
    }

    override fun reverse() {
        projectDirectory.refresh()
        val psiFile = projectDirectory.createFileByName(fileName)

        deletedFileText!!.let {
            psiFile.setText(it)
            deletedFileText = null
        }
    }
}