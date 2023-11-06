package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.research.ideFormerPlugin.api.models.utils.findFileByName

class KtFileClasses(
    private val projectDirectory: PsiDirectory,
    private val fileName: String
) : FileClasses {
    private var ktClasses: List<KtClass>? = null

    private fun PsiFile.ktClasses(): List<KtClass> =
        PsiTreeUtil.findChildrenOfType(this, KtClass::class.java).toList()

    override fun execute() {
        val ktFile = projectDirectory.findFileByName(fileName)
        ktClasses = ktFile.ktClasses()
    }

    override fun getClassesNames(): List<String>? = ktClasses?.mapNotNull { it.name }

    override fun getClassCode(className: String): String? = ktClasses
        ?.firstOrNull { it.name == className }
        ?.text
}