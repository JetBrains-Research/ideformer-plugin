package org.jetbrains.research.ideFormerPlugin.api.models

interface IdeApiMethod {
    fun execute()
}

interface ReversibleApiMethod : IdeApiMethod {
    fun reverse()
}