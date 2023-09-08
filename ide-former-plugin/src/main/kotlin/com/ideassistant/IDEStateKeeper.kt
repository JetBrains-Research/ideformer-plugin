package com.ideassistant

import com.intellij.openapi.project.Project
import java.util.*

class IDEStateKeeper {
    private val apiCallStack: Stack<ReversibleApiMethod> = Stack<ReversibleApiMethod>()
    lateinit var userProject: Project
    lateinit var curDirectory: String

    fun initProject(userProject: Project) {
        this.userProject = userProject
        // TODO: to think about null project path processing
        this.curDirectory = userProject.projectFilePath ?: ""
    }

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