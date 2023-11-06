package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.research.ideFormerPlugin.api.models.utils.findFileByName

class JavaFileFunctions(
    private val projectDirectory: PsiDirectory,
    private val javaFileName: String
) : FileFunctions {
    private var javaFunctions: List<PsiMethod>? = null

    private fun PsiFile.javaFunctions(): List<PsiMethod> =
        PsiTreeUtil.findChildrenOfType(this, PsiMethod::class.java).toList()

    override fun execute() {
        val javaFile = projectDirectory.findFileByName(javaFileName)
        javaFunctions = javaFile.javaFunctions()
    }

    override fun getFunctionsNames(): List<String>? = javaFunctions?.map { it.name }
    override fun getFunctionCode(functionName: String): String? = javaFunctions
        ?.firstOrNull { it.name == functionName }
        ?.text
}