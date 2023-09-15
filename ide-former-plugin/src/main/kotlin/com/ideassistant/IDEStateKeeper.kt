package com.ideassistant

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import java.util.*

class IDEStateKeeper(val userProject: Project) {
    private val apiCallStack: Stack<ReversibleApiMethod> = Stack<ReversibleApiMethod>()
    lateinit var curDirectory: PsiDirectory

    init {
        // TODO: to think about null project path processing
        val projectBaseDir = userProject.guessProjectDir()
        ApplicationManager.getApplication().runReadAction {
            this.curDirectory = projectBaseDir?.let { PsiManager.getInstance(userProject).findDirectory(it) }
                ?: throw Exception("No project file path")
        }
    }

    fun saveReversibleApiCall(apiCall: ReversibleApiMethod) = apiCallStack.addElement(apiCall)

    fun undoLastApiCall() {
        if (apiCallStack.empty()) {
            return
        }

        val lastApiCall = apiCallStack.peek()
        lastApiCall.reverse()
        apiCallStack.pop()
    }
}