package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses

import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.psi.PyClass
import com.jetbrains.python.psi.PyFile
import org.jetbrains.research.ideFormerPlugin.api.findFileByName
import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiMethod

class PyFileClasses(
    projectDirectory: PsiDirectory,
    fileName: String
) : IdeApiMethod {
    private val pyFile: PyFile = projectDirectory.findFileByName(fileName) as PyFile
    private var pyClasses: List<PyClass>? = null

    private fun PyFile.pyClasses(): List<PyClass> =
        PsiTreeUtil.findChildrenOfType(this, PyClass::class.java).toList()

    override fun execute() {
        pyClasses = pyFile.pyClasses()
    }

    fun getPyClassesNames(): List<String>? = pyClasses?.mapNotNull { it.name }

    fun getPyClassCode(pyClassName: String): String? = pyClasses
        ?.firstOrNull { it.name == pyClassName }
        ?.text
}