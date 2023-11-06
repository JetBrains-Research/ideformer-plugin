package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses

import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.childrenOfType
import com.jetbrains.python.psi.PyClass
import org.jetbrains.research.ideFormerPlugin.api.models.utils.findFileByName

class PyFileClasses(
    private val projectDirectory: PsiDirectory,
    private val fileName: String
) : FileClasses {
    private var pyClasses: List<PyClass>? = null

    override fun execute() {
        val psiFile = projectDirectory.findFileByName(fileName)
        pyClasses = psiFile.childrenOfType<PyClass>()
    }

    override fun getClassesNames(): List<String>? = pyClasses?.mapNotNull { it.name }

    override fun getClassCode(className: String): String? = pyClasses
        ?.firstOrNull { it.name == className }
        ?.text
}