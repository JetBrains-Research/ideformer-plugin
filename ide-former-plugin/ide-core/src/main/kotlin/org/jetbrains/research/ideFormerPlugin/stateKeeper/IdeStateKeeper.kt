package org.jetbrains.research.ideFormerPlugin.stateKeeper

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod
import java.util.*

class IdeStateKeeper(val userProject: Project) {
    private val apiMethodStack: Stack<ReversibleApiMethod> = Stack<ReversibleApiMethod>()
    var currentProjectDirectory: PsiDirectory = findProjectBaseDirectory()

    // TODO: to think about null project path processing
    private fun findProjectBaseDirectory() = userProject.guessProjectDir()?.let { projectBaseDir ->
        ApplicationManager.getApplication().runReadAction<PsiDirectory> {
            PsiManager.getInstance(userProject).findDirectory(projectBaseDir)
        }
    } ?: error("No project file path")

    fun saveReversibleApiMethod(apiMethod: ReversibleApiMethod) = apiMethodStack.addElement(apiMethod)

    fun reverseLastApiMethods(apiMethodsCount: Int): Int {
        repeat(apiMethodsCount) {
            if (apiMethodStack.empty()) {
                return it
            }

            apiMethodStack.pop().reverse()
        }
        return apiMethodsCount
    }
}