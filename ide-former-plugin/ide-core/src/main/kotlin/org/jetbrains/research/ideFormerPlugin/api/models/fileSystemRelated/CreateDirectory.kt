package org.jetbrains.research.ideFormerPlugin.api.models.fileSystemRelated

import com.intellij.psi.PsiDirectory
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod
import org.jetbrains.research.ideFormerPlugin.api.models.utils.createSubdirectoryByName
import org.jetbrains.research.ideFormerPlugin.api.models.utils.deleteSubdirectoryByName

class CreateDirectory(
    private val projectDirectory: PsiDirectory,
    private val directoryName: String
) : ReversibleApiMethod {

    override fun execute() {
        projectDirectory.createSubdirectoryByName(directoryName)
    }

    override fun reverse() {
        projectDirectory.deleteSubdirectoryByName(directoryName)
    }
}