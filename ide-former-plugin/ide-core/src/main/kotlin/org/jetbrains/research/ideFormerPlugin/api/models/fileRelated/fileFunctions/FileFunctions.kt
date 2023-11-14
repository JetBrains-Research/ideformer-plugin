package org.jetbrains.research.ideFormerPlugin.api.models.fileRelated.fileFunctions

import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiMethod

interface FileFunctions : IdeApiMethod {
    fun getFunctionsNames(): List<String>?
    fun getFunctionCode(functionName: String): String
}