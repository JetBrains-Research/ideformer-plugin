package com.ideassistant

import java.util.*

class IDEStateKeeper {
    private val apiCallStack: Stack<Reversable> = Stack<Reversable>()

    fun saveApiCall(apiCall: IDEApiMethod) {
        if (apiCall is Reversable) {
            apiCallStack.push(apiCall)
        }
    }

    fun undoLastApiCall() {
        if (apiCallStack.empty()) {
            return
        }

        val lastApiCall = apiCallStack.peek()
        lastApiCall.reverse()
        apiCallStack.pop()
    }
}

val ideStateKeeper = IDEStateKeeper()