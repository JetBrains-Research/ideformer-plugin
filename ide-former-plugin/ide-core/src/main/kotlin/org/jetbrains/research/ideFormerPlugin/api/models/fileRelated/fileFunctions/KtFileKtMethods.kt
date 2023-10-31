package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions

import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.research.ideFormerPlugin.api.findFileByName
import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiMethod

class KtFileKtMethods(
    projectDirectory: PsiDirectory,
    ktFileName: String
) : IdeApiMethod {
    private val ktFile: KtFile = projectDirectory.findFileByName(ktFileName) as KtFile
    private var fileKtMethods: List<KtNamedFunction>? = null

    private fun KtFile.ktNamedFunctions(): List<KtNamedFunction> =
        PsiTreeUtil.findChildrenOfType(this, KtNamedFunction::class.java).toList()

    override fun execute() {
        fileKtMethods = ktFile.ktNamedFunctions()
    }

    fun getFileKtMethodsNames() = fileKtMethods?.map { it.name }
}