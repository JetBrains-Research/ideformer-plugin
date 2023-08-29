package com.ideassistant

import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextField
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking

class IDEAssistantToolWindowFactory : ToolWindowFactory, DumbAware {
    private class IDEAssistantToolWindow {
        val contentPanel = JPanel()
        private val chatArea = JTextArea(10, 50).apply {
            lineWrap = true
            wrapStyleWord = true
            isEditable = false
        }

        // TODO: change to JTextArea
        private val userInputField = JTextField(30)
        private val ideApiExecutor = IdeApiExecutor(getUserProject())
        private val interactionClient = HttpClient(CIO)

        private fun getUserProject(): Project = ProjectManager.getInstance().openProjects.first()

        init {
            contentPanel.apply {
                add(JLabel("User input:"))
                add(JScrollPane(userInputField))
                add(JScrollPane(chatArea))
            }

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

        suspend fun modelAndIDEInteraction(userQuery: String): String {
            val interactionChain = StringBuilder()

            var prevStepInfo = userQuery
            while (true) {
                // TODO: make a real http request and then extract a model query from the http response
                val modelHttpResponse = interactionClient.get("https://ktor.io/")
                val modelAPIMethodQuery: IDEApiMethod = LLMSimulator.getAPIQuery(prevStepInfo) ?: break
                interactionChain.append("[API Call Info]: $modelAPIMethodQuery\n")

                val apiCallRes = ideApiExecutor.executeApiMethod(modelAPIMethodQuery)
                interactionChain.append("[API Call Res]: $apiCallRes\n")

                ideApiExecutor.updateProject(getUserProject())
                prevStepInfo = apiCallRes
            }

            return interactionChain.toString()
        }

    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val ideToolWindow = IDEAssistantToolWindow()
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(ideToolWindow.contentPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}