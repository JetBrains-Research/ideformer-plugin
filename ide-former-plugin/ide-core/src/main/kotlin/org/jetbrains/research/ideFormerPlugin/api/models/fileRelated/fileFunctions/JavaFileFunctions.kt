package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiMethod
import org.jetbrains.research.ideFormerPlugin.api.models.utils.NO_SUCH_FILE_FUNCTION
import org.jetbrains.research.ideFormerPlugin.api.models.utils.getFilePsiElementsOfType

class JavaFileFunctions(
    private val projectDirectory: PsiDirectory,
    private val javaFileName: String
) : FileFunctions {
    private var javaFunctions: List<PsiMethod>? = null

    override fun execute() {
        javaFunctions = getFilePsiElementsOfType<PsiMethod>(projectDirectory, javaFileName)
    }

    override fun getFunctionsNames(): List<String>? = javaFunctions?.map { it.name }

    override fun getFunctionCode(functionName: String): String = javaFunctions
        ?.firstOrNull { it.name == functionName }
        ?.text
        ?: error(NO_SUCH_FILE_FUNCTION)
}