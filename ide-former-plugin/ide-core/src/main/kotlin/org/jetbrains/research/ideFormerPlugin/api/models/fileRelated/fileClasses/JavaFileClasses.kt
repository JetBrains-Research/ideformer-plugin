package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.childrenOfType
import org.jetbrains.research.ideFormerPlugin.api.models.utils.findFileByName

class JavaFileClasses(
    private val projectDirectory: PsiDirectory,
    private val fileName: String
) : FileClasses {
    private var javaClasses: List<PsiClass>? = null

    override fun execute() {
        val psiFile = projectDirectory.findFileByName(fileName)
        javaClasses = psiFile.childrenOfType<PsiClass>()
    }

    override fun getClassesNames(): List<String>? =
        javaClasses?.mapNotNull { it.name }

    override fun getClassCode(className: String): String? = javaClasses
        ?.firstOrNull { it.name == className }
        ?.text
}