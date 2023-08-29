package com.ideassistant

import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import kotlinx.coroutines.runBlocking
import javax.swing.*

class IDEAssistantToolWindowFactory : ToolWindowFactory, DumbAware {
    private class IDEAssistantToolWindow(userProject: Project) {
        val contentPanel = JPanel()
        private val chatArea = JTextArea(10, 50).apply {
            lineWrap = true
            wrapStyleWord = true
            isEditable = false
        }

        // TODO: change to JTextArea
        private val userInputField = JTextField(30)

        // TODO: move to the IDE server or the separate service (?)
        private val ideApiExecutor = IdeApiExecutor(userProject)

        init {
            contentPanel.apply {
                add(JLabel("User input:"))
                add(JScrollPane(userInputField))
                add(JScrollPane(chatArea))
            }

            userProject.service<ProjectService>().start()
            startDialogue()
        }

        private fun startDialogue() {
            userInputField.addActionListener {
                val userQuery = userInputField.text
                val modelResponse = runBlocking {
                    modelAndIDEInteraction(userQuery)
                }

                invokeLater {
                    chatArea.append("User: $userQuery\n")
                    chatArea.append("Assistant: $modelResponse\n")
                    userInputField.text = ""
                }
            }
        }

        // TODO: move from ToolWindow to service/ server part
        fun modelAndIDEInteraction(userQuery: String): String {
            val interactionChain = StringBuilder()

            var prevStepInfo = userQuery
            while (true) {
                // TODO: make a real http request and then extract a model query from the http response
                val modelAPIMethodQuery: IDEApiMethod = LLMSimulator.getAPIQuery(prevStepInfo) ?: break
                interactionChain.append("[API Call Info]: $modelAPIMethodQuery\n")

                val apiCallRes = ideApiExecutor.executeApiMethod(modelAPIMethodQuery)
                interactionChain.append("[API Call Res]: $apiCallRes\n")

                prevStepInfo = apiCallRes
            }

            return interactionChain.toString()
        }

    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val ideToolWindow = IDEAssistantToolWindow(project)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(ideToolWindow.contentPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}