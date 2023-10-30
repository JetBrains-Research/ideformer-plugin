package org.jetbrains.research.ideFormerPlugin.api.models

import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

class KtFileKtMethods(
    private val projectDirectory: PsiDirectory,
    private val ktFileName: String
) : IdeApiMethod {
    private var fileKtMethods: List<KtNamedFunction>? = null

    private fun findKtFileByName(ktFileName: String): KtFile =
        projectDirectory.files
            .filterIsInstance<KtFile>()
            .firstOrNull { it.name == ktFileName }
            ?: error("No such file in the current directory")

    private fun KtFile.ktNamedFunctions(): List<KtNamedFunction> =
        PsiTreeUtil.findChildrenOfType(this, KtNamedFunction::class.java).toList()

    override fun execute() {
        findKtFileByName(ktFileName).also {
            fileKtMethods = it.ktNamedFunctions()
        }
    }

    fun getFileKtMethodsNames() = fileKtMethods?.map { it.name }
}