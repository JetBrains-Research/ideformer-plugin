package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions

import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.psi.PyFile
import com.jetbrains.python.psi.PyFunction
import org.jetbrains.research.ideFormerPlugin.api.models.utils.findFileByName

class PyFileFunctions(
    projectDirectory: PsiDirectory,
    pyFileName: String
) : FileFunctions {
    private val pyFile: PyFile = projectDirectory.findFileByName(pyFileName) as PyFile
    private var fileFunctions: List<PyFunction>? = null

    private fun PyFile.pyFunctions(): List<PyFunction> =
        PsiTreeUtil.findChildrenOfType(this, PyFunction::class.java).toList()

    override fun execute() {
        fileFunctions = pyFile.pyFunctions()
    }

    override fun getFunctionsNames(): List<String>? = fileFunctions?.mapNotNull { it.name }
    override fun getFunctionCode(functionName: String): String? = fileFunctions
        ?.firstOrNull { it.name == functionName }
        ?.text
}