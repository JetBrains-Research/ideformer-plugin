package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions

import com.intellij.psi.PsiDirectory
import com.jetbrains.python.psi.PyFile
import com.jetbrains.python.psi.PyFunction
import org.jetbrains.research.ideFormerPlugin.api.models.utils.findFileByName
import org.jetbrains.research.ideFormerPlugin.api.models.utils.psiElementsOfType

class PyFileFunctions(
    private val projectDirectory: PsiDirectory,
    private val pyFileName: String
) : FileFunctions {
    private var pyFunctions: List<PyFunction>? = null

    override fun execute() {
        val pyFile = projectDirectory.findFileByName(pyFileName) as PyFile
        pyFunctions = pyFile.psiElementsOfType<PyFunction>()
    }

    override fun getFunctionsNames(): List<String>? = pyFunctions?.mapNotNull { it.name }
    override fun getFunctionCode(functionName: String): String? = pyFunctions
        ?.firstOrNull { it.name == functionName }
        ?.text
}