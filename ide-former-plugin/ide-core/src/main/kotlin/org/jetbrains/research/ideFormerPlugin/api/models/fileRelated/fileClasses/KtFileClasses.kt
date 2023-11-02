package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses

import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.research.ideFormerPlugin.api.findFileByName
import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiMethod

class KtFileClasses(
    projectDirectory: PsiDirectory,
    fileName: String
) : IdeApiMethod {
    private val ktFile: KtFile = projectDirectory.findFileByName(fileName) as KtFile
    private var ktClasses: List<KtClass>? = null

    private fun KtFile.ktClasses(): List<KtClass> =
        PsiTreeUtil.findChildrenOfType(this, KtClass::class.java).toList()

    override fun execute() {
        ktClasses = ktFile.ktClasses()
    }

    fun getKtClassesNames(): List<String>? = ktClasses?.mapNotNull { it.name }

    fun getKtClassCode(ktClassName: String): String? = ktClasses
        ?.firstOrNull { it.name == ktClassName }
        ?.text
}