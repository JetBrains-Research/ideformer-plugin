package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.research.ideFormerPlugin.api.findFileByName
import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiMethod

class JavaFileFunctions(
    projectDirectory: PsiDirectory,
    ktFileName: String
) : IdeApiMethod {
    private val javaFile: PsiFile = projectDirectory.findFileByName(ktFileName)
    private var fileFunctions: List<PsiMethod>? = null

    private fun PsiFile.javaFunctions(): List<PsiMethod> =
        PsiTreeUtil.findChildrenOfType(this, PsiMethod::class.java).toList()

    override fun execute() {
        fileFunctions = javaFile.javaFunctions()
    }

    fun getFileFunctionsNames(): List<String>? = fileFunctions?.map { it.name }
}