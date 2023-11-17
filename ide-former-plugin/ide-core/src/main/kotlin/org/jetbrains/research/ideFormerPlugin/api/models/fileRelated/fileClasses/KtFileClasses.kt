package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses

import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.research.ideFormerPlugin.api.models.utils.*

class KtFileClasses(
    private val projectDirectory: PsiDirectory,
    private val ktFileName: String
) : FileClasses {
    private var ktClasses: List<KtClass>? = null

    override fun execute() {
        ktClasses = getFilePsiElementsOfType<KtClass>(projectDirectory, ktFileName)
    }

    override fun getClassesNames(): List<String> {
        if (ktClasses == null) error(UNCALLED_EXECUTE_BEFORE_RESULT_GETTING)
        return ktClasses!!.mapNotNull { it.name }
    }

    override fun getClassCode(className: String): String {
        if (ktClasses == null) error(UNCALLED_EXECUTE_BEFORE_RESULT_GETTING)

        return ktClasses!!
            .firstOrNull { it.name == className }
            ?.text
            ?: error(NO_SUCH_FILE_CLASS)
    }
}