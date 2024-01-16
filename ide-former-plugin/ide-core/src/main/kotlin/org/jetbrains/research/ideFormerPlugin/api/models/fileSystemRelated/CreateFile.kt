package org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated

import com.intellij.psi.PsiDirectory
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod
import org.jetbrains.research.ideFormerPlugin.api.models.utils.*

class CreateFile(
    private val projectDirectory: PsiDirectory,
    private val fileName: String
) : ReversibleApiMethod {

    override fun execute() {
        projectDirectory.refresh()
        projectDirectory.createFileByName(fileName)
    }

    override fun reverse() {
        projectDirectory.refresh()
        projectDirectory.deleteFileByName(fileName)
    }
}