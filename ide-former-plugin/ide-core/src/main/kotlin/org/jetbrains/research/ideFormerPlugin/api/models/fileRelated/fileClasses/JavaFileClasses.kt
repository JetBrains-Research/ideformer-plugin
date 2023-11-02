package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.research.ideFormerPlugin.api.models.utils.findFileByName

class JavaFileClasses(
    private val projectDirectory: PsiDirectory,
    private val fileName: String
) : FileClasses {
    private var javaClasses: List<PsiClass>? = null

    private fun PsiJavaFile.javaClasses(): List<PsiClass> =
        PsiTreeUtil.findChildrenOfType(this, PsiClass::class.java).toList()

    override fun execute() {
        val javaFile = projectDirectory.findFileByName(fileName) as PsiJavaFile
        javaClasses = javaFile.javaClasses()
    }

    override fun getClassesNames(): List<String>? =
        javaClasses?.mapNotNull { it.name }

    override fun getClassCode(className: String): String? = javaClasses
        ?.firstOrNull { it.name == className }
        ?.text
}