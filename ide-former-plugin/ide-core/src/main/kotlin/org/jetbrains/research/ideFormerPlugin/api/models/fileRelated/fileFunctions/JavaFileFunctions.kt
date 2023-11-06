package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiMethod
import org.jetbrains.research.ideFormerPlugin.api.models.utils.findFileByName
import org.jetbrains.research.ideFormerPlugin.api.models.utils.psiElementsOfType

class JavaFileFunctions(
    private val projectDirectory: PsiDirectory,
    private val javaFileName: String
) : FileFunctions {
    private var javaFunctions: List<PsiMethod>? = null

    override fun execute() {
        val psiFile = projectDirectory.findFileByName(javaFileName)
        javaFunctions = psiFile.psiElementsOfType<PsiMethod>()
    }

    override fun getFunctionsNames(): List<String>? = javaFunctions?.map { it.name }
    override fun getFunctionCode(functionName: String): String? = javaFunctions
        ?.firstOrNull { it.name == functionName }
        ?.text
}