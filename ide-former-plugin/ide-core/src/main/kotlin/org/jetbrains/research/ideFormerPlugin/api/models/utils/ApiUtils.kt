package org.jetbrains.research.ideFormerPlugin.api.models.utils

import com.intellij.psi.PsiDirectory
import com.intellij.util.PathUtil
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses.*
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions.*

const val DEFAULT_DIRECTORY_NAME = "."

const val UNCALLED_EXECUTE_BEFORE_RESULT_GETTING =
    "Execute() must be called before getting the result. The property storing the result has not been initialized yet"
const val UNSUPPORTED_FILE_EXTENSION_ERROR = "Unsupported file extension: only .py, .kt, .java file can be processed"

enum class FileExtensions(val extension: String) {
    KT("kt"),
    JAVA("java"),
    PY("py")
}

fun chooseFileClassesApiForFile(fileName: String, projectDirectory: PsiDirectory): FileClasses =
    when (val fileExtension = PathUtil.getFileExtension(fileName)) {
        FileExtensions.KT.extension -> KtFileClasses(projectDirectory, fileName)
        FileExtensions.JAVA.extension -> JavaFileClasses(projectDirectory, fileName)
        FileExtensions.PY.extension -> PyFileClasses(projectDirectory, fileName)
        else -> error("$UNSUPPORTED_FILE_EXTENSION_ERROR: $fileExtension")
    }

fun chooseFileFunctionsApiForFile(fileName: String, projectDirectory: PsiDirectory): FileFunctions =
    when (val fileExtension = PathUtil.getFileExtension(fileName)) {
        FileExtensions.KT.extension -> KtFileFunctions(projectDirectory, fileName)
        FileExtensions.JAVA.extension -> JavaFileFunctions(projectDirectory, fileName)
        FileExtensions.PY.extension -> PyFileFunctions(projectDirectory, fileName)
        else -> error("$UNSUPPORTED_FILE_EXTENSION_ERROR: $fileExtension")
    }