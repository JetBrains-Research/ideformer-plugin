package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions

import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.research.ideFormerPlugin.api.models.utils.findFileByName

class KtFileFunctions(
    private val projectDirectory: PsiDirectory,
    private val ktFileName: String
) : FileFunctions {
    private var fileFunctions: List<KtFunction>? = null

    private fun KtFile.ktFunctions(): List<KtFunction> =
        PsiTreeUtil.findChildrenOfType(this, KtFunction::class.java).toList()

    override fun execute() {
        val ktFile = projectDirectory.findFileByName(ktFileName) as KtFile
        fileFunctions = ktFile.ktFunctions()
    }

    override fun getFunctionsNames(): List<String>? = fileFunctions?.mapNotNull { it.name }
    override fun getFunctionCode(functionName: String): String? = fileFunctions
        ?.firstOrNull { it.name == functionName }
        ?.text
}