package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions

import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.psi.PyFile
import com.jetbrains.python.psi.PyFunction
import org.jetbrains.research.ideFormerPlugin.api.models.utils.findFileByName

class PyFileFunctions(
    private val projectDirectory: PsiDirectory,
    private val pyFileName: String
) : FileFunctions {
    private var fileFunctions: List<PyFunction>? = null

    private fun PyFile.pyFunctions(): List<PyFunction> =
        PsiTreeUtil.findChildrenOfType(this, PyFunction::class.java).toList()

    override fun execute() {
        val pyFile = projectDirectory.findFileByName(pyFileName) as PyFile
        fileFunctions = pyFile.pyFunctions()
    }

    override fun getFunctionsNames(): List<String>? = fileFunctions?.mapNotNull { it.name }
    override fun getFunctionCode(functionName: String): String? = fileFunctions
        ?.firstOrNull { it.name == functionName }
        ?.text
}