package com.ideassistant

import java.util.*

class IDEStateKeeper {
    private val apiCallStack: Stack<IDEApiMethod> = Stack<IDEApiMethod>()

    fun saveApiCall(apiCall: IDEApiMethod) {
        if (apiCall is Reversable) {
            apiCallStack.push(apiCall)
        }
    }
}

val ideStateKeeper = IDEStateKeeper()