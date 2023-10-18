package org.jetbrains.research.ideFormerPlugin.stateKeeper

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import org.jetbrains.research.ideFormerPlugin.api.ReversibleApiMethod
import java.util.*

class IdeStateKeeper(val userProject: Project) {
    private val apiCallStack: Stack<ReversibleApiMethod> = Stack<ReversibleApiMethod>()
    var currentProjectDirectory: PsiDirectory = findProjectBaseDirectory()

    // TODO: to think about null project path processing
    private fun findProjectBaseDirectory() = userProject.guessProjectDir()?.let { projectBaseDir ->
        ApplicationManager.getApplication().runReadAction<PsiDirectory> {
            PsiManager.getInstance(userProject).findDirectory(projectBaseDir)
        }
    } ?: error("No project file path")

    fun saveReversibleApiCall(apiCall: ReversibleApiMethod) = apiCallStack.addElement(apiCall)

    fun undoApiCalls(apiCallsCount: Int): Int {
        for (i in 1..apiCallsCount) {
            if (apiCallStack.empty()) {
                return i
            }

            val lastApiCall = apiCallStack.peek()
            lastApiCall.reverse()
            apiCallStack.pop()
        }
        return apiCallsCount
    }
}