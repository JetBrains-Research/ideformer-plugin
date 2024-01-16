package org.jetbrains.research.ideFormerPlugin.api.models.gitRelated

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.GitCommand

class GitAdd(
    project: Project,
    projectGitRoot: VirtualFile,
    fileNames: List<String>
) : GitApiMethod(project, projectGitRoot) {
    override var gitCommand: GitCommand? = GitCommand.ADD
    override var gitCommandParameters: List<String>? = fileNames

    override var gitReverseCommand: GitCommand? = GitCommand.RM
    override var gitReverseCommandParameters: List<String>? = listOf("--cached") + fileNames
}