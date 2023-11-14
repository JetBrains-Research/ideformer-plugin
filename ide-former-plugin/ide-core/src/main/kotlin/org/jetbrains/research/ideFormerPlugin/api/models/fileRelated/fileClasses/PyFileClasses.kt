package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses

import com.intellij.psi.PsiDirectory
import com.jetbrains.python.psi.PyClass
import org.jetbrains.research.ideFormerPlugin.api.models.utils.NO_SUCH_FILE_CLASS
import org.jetbrains.research.ideFormerPlugin.api.models.utils.getFilePsiElementsOfType

class PyFileClasses(
    private val projectDirectory: PsiDirectory,
    private val pyFileName: String
) : FileClasses {
    private var pyClasses: List<PyClass>? = null

    override fun execute() {
        pyClasses = getFilePsiElementsOfType<PyClass>(projectDirectory, pyFileName)
    }

    override fun getClassesNames(): List<String>? = pyClasses?.mapNotNull { it.name }

    override fun getClassCode(className: String): String = pyClasses
        ?.firstOrNull { it.name == className }
        ?.text
        ?: error(NO_SUCH_FILE_CLASS)
}