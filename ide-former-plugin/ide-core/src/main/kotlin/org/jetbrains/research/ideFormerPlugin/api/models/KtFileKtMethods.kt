package org.jetbrains.research.ideFormerPlugin.api.models

import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

class KtFileKtMethods(
    private val projectDirectory: PsiDirectory,
    private val ktFileName: String
) : IdeApiMethod {
    private lateinit var fileKtMethods: List<KtNamedFunction>

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

    internal fun getMethodsNames() = fileKtMethods.map { it.name }
    override fun executionResult(): String = getMethodsNames().toString()
}