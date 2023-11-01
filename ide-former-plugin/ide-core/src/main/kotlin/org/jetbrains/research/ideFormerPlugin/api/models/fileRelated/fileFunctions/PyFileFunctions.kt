package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions

import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiMethod
import com.jetbrains.python.psi.PyFunction
import com.jetbrains.python.psi.PyFile
import org.jetbrains.research.ideFormerPlugin.api.findFileByName

class PyFileFunctions(
    projectDirectory: PsiDirectory,
    pyFileName: String
) : IdeApiMethod {
    private val pyFile: PyFile = projectDirectory.findFileByName(pyFileName) as PyFile
    private var fileFunctions: List<PyFunction>? = null

    private fun PyFile.pyFunctions(): List<PyFunction> =
        PsiTreeUtil.findChildrenOfType(this, PyFunction::class.java).toList()

    override fun execute() {
        fileFunctions = pyFile.pyFunctions()
    }

    fun getFileFunctionsNames(): List<String>? = fileFunctions?.mapNotNull { it.name }
}