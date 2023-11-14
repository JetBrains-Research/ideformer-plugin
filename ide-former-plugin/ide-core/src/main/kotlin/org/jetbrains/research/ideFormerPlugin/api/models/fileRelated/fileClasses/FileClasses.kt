package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileClasses

import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiMethod

interface FileClasses : IdeApiMethod {
    fun getClassesNames(): List<String>?
    fun getClassCode(className: String): String
}