package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions

import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.research.ideFormerPlugin.api.models.utils.*

class KtFileFunctions(
    private val projectDirectory: PsiDirectory,
    private val ktFileName: String
) : FileFunctions {
    private var ktFunctions: List<KtFunction>? = null

    override fun execute() {
        ktFunctions = getFilePsiElementsOfType<KtFunction>(projectDirectory, ktFileName)
    }

    override fun getFunctionsNames(): List<String> {
        if (ktFunctions == null) error(UNCALLED_EXECUTE_BEFORE_RESULT_GETTING)
        return ktFunctions!!.mapNotNull { it.name }
    }

    override fun getFunctionCode(functionName: String): String {
        if (ktFunctions == null) error(UNCALLED_EXECUTE_BEFORE_RESULT_GETTING)
        return ktFunctions!!
            .firstOrNull { it.name == functionName }
            ?.text
            ?: error(NO_SUCH_FILE_FUNCTION)
    }
}