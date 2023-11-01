package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions

import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.research.ideFormerPlugin.api.findFileByName
import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiMethod

class KtFileFunctions(
    projectDirectory: PsiDirectory,
    ktFileName: String
) : IdeApiMethod {
    private val ktFile: KtFile = projectDirectory.findFileByName(ktFileName) as KtFile
    private var fileFunctions: List<KtFunction>? = null

    private fun KtFile.ktFunctions(): List<KtFunction> =
        PsiTreeUtil.findChildrenOfType(this, KtFunction::class.java).toList()

    override fun execute() {
        fileFunctions = ktFile.ktFunctions()
    }

    fun getFileFunctionsNames(): List<String>? = fileFunctions?.mapNotNull { it.name }
}