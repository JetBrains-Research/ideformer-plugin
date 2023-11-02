package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.research.ideFormerPlugin.api.findFileByName
import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiMethod

class JavaFileClasses(
    projectDirectory: PsiDirectory,
    fileName: String
) : IdeApiMethod {
    private val javaFile: PsiJavaFile = projectDirectory.findFileByName(fileName) as PsiJavaFile
    private var fileClasses: List<PsiClass>? = null

    private fun PsiJavaFile.javaClasses(): List<PsiClass> =
        PsiTreeUtil.findChildrenOfType(this, PsiClass::class.java).toList()

    override fun execute() {
        fileClasses = javaFile.javaClasses()
    }

    fun getFileClassesNames(): List<String>? =
        fileClasses?.mapNotNull { it.name }
}