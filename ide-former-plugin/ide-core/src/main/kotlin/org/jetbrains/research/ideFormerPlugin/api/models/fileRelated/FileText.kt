package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated

import com.intellij.psi.PsiDirectory
import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiMethod
import org.jetbrains.research.ideFormerPlugin.api.models.utils.findFileRecursively

class FileText(
    private val projectDirectory: PsiDirectory,
    private val fileName: String
) : IdeApiMethod {
    private var fileText: String? = null

    override fun execute() {
        val psiFile = projectDirectory.findFileRecursively(fileName)
        fileText = psiFile.text
    }

    fun getFileText(): String? = fileText
}