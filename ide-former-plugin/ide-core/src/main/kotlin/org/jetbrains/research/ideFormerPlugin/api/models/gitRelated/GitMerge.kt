package org.jetbrains.research.ideFormerPlugin.api.models.gitRelated

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.GitCommand

class GitMerge(
    project: Project,
    projectGitRoot: VirtualFile,
    mergingBranchName: String
) : GitApiMethod(project, projectGitRoot) {
    // Invariant: you're staying on the right branch that you want to merge changes to. Git checkout was made recently.
    override var gitCommand: GitCommand? = GitCommand.MERGE
    override var gitCommandParameters: List<String>? = listOf(mergingBranchName)

    override var gitReverseCommand: GitCommand? = GitCommand.RESET
    // --merge as opposed to the --hard doesn't reset uncommited/ unstashed files.
    // ORIG_HEAD will point to a commit directly before merge has occurred, so you don't have to hunt for it by yourself.
    override var gitReverseCommandParameters: List<String>? = listOf("--merge", "ORIG_HEAD")
}