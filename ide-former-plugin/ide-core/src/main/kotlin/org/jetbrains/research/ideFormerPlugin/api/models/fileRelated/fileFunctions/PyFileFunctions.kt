package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions

import com.intellij.psi.PsiDirectory
import com.jetbrains.python.psi.PyFunction
import org.jetbrains.research.ideFormerPlugin.api.models.utils.*

class PyFileFunctions(
    private val projectDirectory: PsiDirectory,
    private val pyFileName: String
) : FileFunctions {
    private var pyFunctions: List<PyFunction>? = null

    override fun execute() {
        pyFunctions = getFilePsiElementsOfType<PyFunction>(projectDirectory, pyFileName)
    }

    override fun getFunctionsNames(): List<String> {
        if (pyFunctions == null) error(UNCALLED_EXECUTE_BEFORE_RESULT_GETTING)
        return pyFunctions!!.mapNotNull { it.name }
    }

    override fun getFunctionCode(functionName: String): String {
        if (pyFunctions == null) error(UNCALLED_EXECUTE_BEFORE_RESULT_GETTING)
        return pyFunctions!!
            .firstOrNull { it.name == functionName }
            ?.text
            ?: error(NO_SUCH_FILE_FUNCTION)
    }
}