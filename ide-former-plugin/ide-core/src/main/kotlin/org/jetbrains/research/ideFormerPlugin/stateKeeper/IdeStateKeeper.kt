package org.jetbrains.research.ideFormerPlugin.stateKeeper

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import git4idea.GitVcs
import git4idea.actions.GitPull
import git4idea.branch.GitBranchUtil
import git4idea.repo.GitRepository
import org.jetbrains.research.ideFormerPlugin.api.models.ReversibleApiMethod
import org.jetbrains.research.ideFormerPlugin.api.models.utils.NO_GIT_REPO_FOR_PROJECT
import org.jetbrains.research.ideFormerPlugin.api.models.utils.NO_GIT_ROOTS
import java.util.*

class IdeStateKeeper(val userProject: Project) {
    private val apiMethodStack: Stack<ReversibleApiMethod> = Stack<ReversibleApiMethod>()
    var currentProjectDirectory: PsiDirectory = findProjectBaseDirectory()

    private val projectGitRepo: GitRepository
    val projectGitRoot: VirtualFile

    companion object {
        const val DEFAULT_API_METHODS_COUNT_TO_REVERSE = 1
    }

    init {
        val gitVcs = GitVcs.getInstance(userProject)
        val gitRoots = GitPull.getGitRoots(userProject, gitVcs)
        if (gitRoots.isNullOrEmpty()) error(NO_GIT_ROOTS)

        projectGitRepo = GitBranchUtil.guessRepositoryForOperation(userProject, DataContext.EMPTY_CONTEXT)
            ?: error(NO_GIT_REPO_FOR_PROJECT)
        projectGitRoot = projectGitRepo.root
    }

    // TODO: to think about null project path processing
    private fun findProjectBaseDirectory() = userProject.guessProjectDir()?.let { projectBaseDir ->
        ApplicationManager.getApplication().runReadAction<PsiDirectory> {
            PsiManager.getInstance(userProject).findDirectory(projectBaseDir)
        }
    } ?: error("No project file path")

    fun saveReversibleApiMethod(apiMethod: ReversibleApiMethod) = apiMethodStack.addElement(apiMethod)

    fun reverseLastApiMethods(apiMethodsCount: Int = DEFAULT_API_METHODS_COUNT_TO_REVERSE): Int {
        repeat(apiMethodsCount) {
            if (apiMethodStack.empty()) {
                return it
            }

            apiMethodStack.pop().reverse()
        }
        return apiMethodsCount
    }
}