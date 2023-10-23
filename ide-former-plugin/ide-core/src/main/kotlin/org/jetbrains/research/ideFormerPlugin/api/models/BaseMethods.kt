package org.jetbrains.research.ideFormerPlugin.api.models

interface IdeApiMethod {
    fun execute()
    fun executionResult(): String
}

interface ReversibleApiMethod : IdeApiMethod {
    fun reverse()
}