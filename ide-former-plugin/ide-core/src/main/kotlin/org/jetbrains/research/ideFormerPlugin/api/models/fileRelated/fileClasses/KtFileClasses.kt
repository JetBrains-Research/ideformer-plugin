package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses

import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.childrenOfType
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.research.ideFormerPlugin.api.models.utils.findFileByName

class KtFileClasses(
    private val projectDirectory: PsiDirectory,
    private val fileName: String
) : FileClasses {
    private var ktClasses: List<KtClass>? = null

    override fun execute() {
        val psiFile = projectDirectory.findFileByName(fileName)
        ktClasses = psiFile.childrenOfType<KtClass>()
    }

    override fun getClassesNames(): List<String>? = ktClasses?.mapNotNull { it.name }

    override fun getClassCode(className: String): String? = ktClasses
        ?.firstOrNull { it.name == className }
        ?.text
}