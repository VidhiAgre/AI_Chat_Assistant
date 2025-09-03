package com.example.aichatassistant

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ServerException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel()  {

    val messageList by lazy{
        mutableStateListOf<MessageModel>()
    }


    private val _recognizedText = MutableStateFlow("")
    val recognizedText = _recognizedText.asStateFlow()

    fun onSpeechResult(text: String) {
        _recognizedText.value = text
    }

    fun clearRecognizedText() {
        _recognizedText.value = ""
    }


    private val generativeModel : GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash", // Using a valid and recommended model
        apiKey = Constants.apiKey
    )

    fun sendMessage(question: String) {
        if (question.isBlank()) {
            return
        }

        viewModelScope.launch{
            messageList.add(MessageModel(question, "user"))
            val loadingMessage = MessageModel("typing...", "model")
            messageList.add(loadingMessage)

            try{
                val chat = generativeModel.startChat(
                    history = messageList.dropLast(2).map {
                        content(it.role){ text(it.message) }
                    }.toList()
                )

                val response = chat.sendMessage(question)
                val responseText = response.text

                messageList.remove(loadingMessage)
                if (responseText != null) {
                    messageList.add(MessageModel(responseText, "model"))
                } else {
                    messageList.add(MessageModel("Received an empty response from the model.", "model"))
                }

            } catch(e: ServerException) {
                messageList.remove(loadingMessage)
                Log.e("ChatViewModel", "ServerException: ${e.message}")
                messageList.add(MessageModel("Error: A server error occurred. Please check your API key and network connection.", "model"))
            } catch(e: Exception){
                messageList.remove(loadingMessage)
                Log.e("ChatViewModel", "Exception: ${e.message}")
                messageList.add(MessageModel("Error: An unexpected error occurred: ${e.localizedMessage}", "model"))
            }
        }
    }
}