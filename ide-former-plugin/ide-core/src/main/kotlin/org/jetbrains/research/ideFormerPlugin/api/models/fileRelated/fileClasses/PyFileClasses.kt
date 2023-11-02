package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses

import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.psi.PyClass
import com.jetbrains.python.psi.PyFile
import org.jetbrains.research.ideFormerPlugin.api.models.utils.findFileByName

class PyFileClasses(
    private val projectDirectory: PsiDirectory,
    private val fileName: String
) : FileClasses {
    private var pyClasses: List<PyClass>? = null

    private fun PyFile.pyClasses(): List<PyClass> =
        PsiTreeUtil.findChildrenOfType(this, PyClass::class.java).toList()

    override fun execute() {
        val pyFile = projectDirectory.findFileByName(fileName) as PyFile
        pyClasses = pyFile.pyClasses()
    }

    override fun getClassesNames(): List<String>? = pyClasses?.mapNotNull { it.name }

    override fun getClassCode(className: String): String? = pyClasses
        ?.firstOrNull { it.name == className }
        ?.text
}