package com.ideassistant

import java.util.*

class IDEStateKeeper {
    private val apiCallStack: Stack<ReversibleApiMethod> = Stack<ReversibleApiMethod>()

    fun saveApiCall(apiCall: IDEApiMethod) {
        if (apiCall is ReversibleApiMethod) {
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