package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileText

import com.intellij.psi.PsiDirectory
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod
import org.jetbrains.research.ideFormerPlugin.api.models.utils.*

class SetFileText(
    private val projectDirectory: PsiDirectory,
    private val fileName: String,
    private val newText: String
) : ReversibleApiMethod {
    private var previousText: String? = null

    override fun execute() {
        projectDirectory.virtualFile.refresh(false, false)
        val psiFile = projectDirectory.findFileRecursively(fileName)
        previousText = psiFile.text
        psiFile.setText(newText)
    }

    override fun reverse() {
        val psiFile = projectDirectory.findFileRecursively(fileName)
        if (previousText == null) error(UNCALLED_EXECUTE_BEFORE_RESULT_GETTING)
        psiFile.setText(previousText!!)
    }
}