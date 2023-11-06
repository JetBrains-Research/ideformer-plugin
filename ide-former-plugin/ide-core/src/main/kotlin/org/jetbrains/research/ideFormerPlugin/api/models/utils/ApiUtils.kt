package org.jetbrains.research.ideFormerPlugin.api.models.utils

import com.intellij.psi.PsiDirectory
import com.intellij.util.PathUtil
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses.FileClasses
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses.JavaFileClasses
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses.KtFileClasses
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses.PyFileClasses
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions.FileFunctions
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions.JavaFileFunctions
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions.KtFileFunctions
import org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions.PyFileFunctions

const val DEFAULT_DIRECTORY_NAME = "."

fun chooseFileClassesApiForFile(fileName: String, projectDirectory: PsiDirectory): FileClasses =
    when (val fileExtension = PathUtil.getFileExtension(fileName)) {
        "kt" -> KtFileClasses(projectDirectory, fileName)
        "java" -> JavaFileClasses(projectDirectory, fileName)
        "py" -> PyFileClasses(projectDirectory, fileName)
        else -> error("Unsupported file extension $fileExtension")
    }

fun chooseFileFunctionsApiForFile(fileName: String, projectDirectory: PsiDirectory): FileFunctions =
    when (val fileExtension = PathUtil.getFileExtension(fileName)) {
        "kt" -> KtFileFunctions(projectDirectory, fileName)
        "java" -> JavaFileFunctions(projectDirectory, fileName)
        "py" -> PyFileFunctions(projectDirectory, fileName)
        else -> error("Unsupported file extension: $fileExtension")
    }