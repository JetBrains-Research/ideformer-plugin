package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import org.jetbrains.research.ideFormerPlugin.api.models.utils.getFilePsiElementsOfType

class JavaFileClasses(
    private val projectDirectory: PsiDirectory,
    private val javaFileName: String
) : FileClasses {
    private var javaClasses: List<PsiClass>? = null

    override fun execute() {
        javaClasses = getFilePsiElementsOfType<PsiClass>(projectDirectory, javaFileName)
    }

    override fun getClassesNames(): List<String>? =
        javaClasses?.mapNotNull { it.name }

    override fun getClassCode(className: String): String? = javaClasses
        ?.firstOrNull { it.name == className }
        ?.text
}