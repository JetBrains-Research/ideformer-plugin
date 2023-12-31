package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses

import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.research.ideFormerPlugin.api.models.utils.getFilePsiElementsOfType

class KtFileClasses(
    private val projectDirectory: PsiDirectory,
    private val ktFileName: String
) : FileClasses {
    private var ktClasses: List<KtClass>? = null

    override fun execute() {
        ktClasses = getFilePsiElementsOfType<KtClass>(projectDirectory, ktFileName)
    }

    override fun getClassesNames(): List<String>? = ktClasses?.mapNotNull { it.name }

    override fun getClassCode(className: String): String? = ktClasses
        ?.firstOrNull { it.name == className }
        ?.text
}