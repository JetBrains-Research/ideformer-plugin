package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import org.jetbrains.research.ideFormerPlugin.api.findFileByName
import org.jetbrains.research.ideFormerPlugin.api.getAllClasses
import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiMethod

class FileClasses(
    projectDirectory: PsiDirectory,
    fileName: String
) : IdeApiMethod {
    private val psiFile: PsiFile = projectDirectory.findFileByName(fileName)
    private var fileClasses: List<PsiClass>? = null
    override fun execute() {
        fileClasses = psiFile.getAllClasses()
    }

    fun getFileClassesNames(): List<String>? =
        fileClasses?.mapNotNull { it.name }
}