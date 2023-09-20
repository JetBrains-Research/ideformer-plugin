package com.ideAssistant.transmitter

import com.ideAssistant.api.IdeApiMethod
import com.ideAssistant.model.LLMSimulator
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*

class UserQueryTransmitter(private val modelServerUrl: String = "localhost:8081/post") {
    // TODO: the method will be used to interact with the model instead of a stub
    suspend fun sendUserQueryToModel(userQuery: String) {
        val client = HttpClient(CIO)
        val response = client.post(modelServerUrl) {
            setBody(userQuery)
        }

        // TODO: add logging
        println("Response status: $response.status")
        client.close()
    }

    fun serverClientInteractionStub(userProject: Project): String {
        val llmSimulator = LLMSimulator(userProject)
        val interactionChain = StringBuilder()

        while (true) {
            val modelAPIMethodQuery: IdeApiMethod = llmSimulator.getAPIQuery() ?: break
            interactionChain.append("[API Call Info]:\n$modelAPIMethodQuery\n")

            modelAPIMethodQuery.execute()
            val apiCallRes = modelAPIMethodQuery.getExecutionRes()
            interactionChain.append("[API Call Res]:\n$apiCallRes\n")
        }

        return interactionChain.toString()
    }
}

@Service
class UserQueryTransmitterService {
    private val transmitter = UserQueryTransmitter()

    suspend fun sendUserQueryToModel(userQuery: String) {
        transmitter.sendUserQueryToModel(userQuery)
    }

    fun serverClientInteractionStub(userProject: Project): String =
        transmitter.serverClientInteractionStub(userProject)
}