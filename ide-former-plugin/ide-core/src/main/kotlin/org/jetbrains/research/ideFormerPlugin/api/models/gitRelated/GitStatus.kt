package org.jetbrains.research.ideFormerPlugin.api.models.gitRelated

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.*
import org.jetbrains.research.ideFormerPlugin.api.models.IdeApiMethod
import org.jetbrains.research.ideFormerPlugin.api.models.utils.UNCALLED_EXECUTE_BEFORE_RESULT_GETTING
import org.jetbrains.research.ideFormerPlugin.api.models.utils.executeGitCommand

class GitStatus(
    private val project: Project,
    private val projectGitRoot: VirtualFile
) : IdeApiMethod {
    private var status: String? = null

    override fun execute() {
        status = executeGitCommand(project, projectGitRoot, GitCommand.STATUS, null)
    }

    fun getStatus(): String {
        if (status == null) error(UNCALLED_EXECUTE_BEFORE_RESULT_GETTING)
        return status!!
    }
}