package com.ideassistant

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
        private val ideApiExecutor = IdeApiExecutor(ProjectManager.getInstance().openProjects.first())

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
                chatArea.append("User: $userQuery\n")

                val modelResponse = modelAndIDEInteraction(userQuery)
                chatArea.append("Assistant: $modelResponse\n")

                userInputField.text = ""
            }
        }

        fun modelAndIDEInteraction(userQuery: String): String {
            val interactionChain = StringBuilder()

            var prevStepInfo = userQuery
            while (true) {
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
        val ideToolWindow = IDEAssistantToolWindow()
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(ideToolWindow.contentPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}