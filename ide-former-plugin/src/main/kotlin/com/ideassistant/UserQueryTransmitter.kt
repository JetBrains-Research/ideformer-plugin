package com.ideassistant

import com.intellij.openapi.components.Service
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

    fun serverClientInteractionStub(): String {
        val llmSimulator = LLMSimulator()
        val interactionChain = StringBuilder()

        while (true) {
            val modelAPIMethodQuery: IDEApiMethod = llmSimulator.getAPIQuery() ?: break
            interactionChain.append("[API Call Info]: $modelAPIMethodQuery\n")

            val apiCallRes = modelAPIMethodQuery.execute()
            interactionChain.append("[API Call Res]: $apiCallRes\n")
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

    fun serverClientInteractionStub(): String =
        transmitter.serverClientInteractionStub()
}