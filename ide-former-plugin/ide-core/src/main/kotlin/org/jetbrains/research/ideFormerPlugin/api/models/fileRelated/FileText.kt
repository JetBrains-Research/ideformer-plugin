package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import org.jetbrains.research.ideFormerPlugin.api.findFileByName
import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiMethod

class FileText(
    projectDirectory: PsiDirectory,
    fileName: String
) : IdeApiMethod {
    private val psiFile: PsiFile = projectDirectory.findFileByName(fileName)
    private var fileText: String? = null

    override fun execute() {
        fileText = psiFile.text
    }

    fun getFileText(): String? = fileText
}