# AI Chat Assistant - Android Application

This Android application enables users to interact with a generative AI model through a chat interface. It supports both text-based and voice-based message input and displays the conversation history.

## Core Functionality

*   **AI-Powered Conversations:** Facilitates interactive chat sessions with a generative AI.
*   **Text Message Input:** Allows users to type and send messages.
*   **Voice Message Input:** Integrates Android's speech recognition for dictating messages.
*   **Conversation Display:** Renders the chat history in a clear, scrollable list format.
*   **User Interface:** Implemented with Jetpack Compose, adhering to Material Design 3 principles for a modern look and feel.

## Technology Stack & Key Components

*   **Programming Language:** Kotlin
*   **User Interface Toolkit:** Jetpack Compose
    *   `Scaffold`, `LazyColumn`, `OutlinedTextField`, `IconButton`, `TopAppBar`
*   **Design System:** Material Design 3
    *   `MaterialTheme`, Color Schemes (`primary`, `secondaryContainer`, etc.)
*   **Artificial Intelligence Integration:** Google Generative AI SDK (e.g., for Gemini models)
    *   Handles sending user prompts and receiving AI-generated responses.
*   **Voice Input:** Android Speech Recognition API
    *   `RecognizerIntent.ACTION_RECOGNIZE_SPEECH`
*   **State Management:** Android ViewModel (`androidx.lifecycle.ViewModel`)
    *   Manages UI-related data like the message list and recognized speech text.
    *   Utilizes `MutableStateFlow` and `StateFlow` for reactive updates.
*   **Asynchronous Operations:** Kotlin Coroutines
    *   `viewModelScope`, `rememberCoroutineScope`, `LaunchedEffect` for background tasks and UI updates.
*   **Build System:** Gradle with Kotlin DSL (`build.gradle.kts`)
*   **Development Environment:** Android Studio

## Project Setup and Execution

1.  **Clone the Repository**
2.  **API Key Configuration:**
    *   An API key for the Google Generative AI service is required. Obtain this from [Google AI Studio](https://aistudio.google.com/) or your provider.
    *   In the root directory of the project, create a file named `local.properties`.
    *   Add your API key to this file.
      **Note: `local.properties` is intentionally excluded from version control via `.gitignore` to protect sensitive keys.**
3.  **Open in Android Studio:**
    *   Launch Android Studio.
    *   Select "Open" and navigate to the cloned project directory.
    *   Allow Android Studio to perform Gradle sync and download necessary dependencies.
4.  **Build and Run:**
    *   Choose an Android Virtual Device (Emulator) or connect a physical Android device.
    *   Ensure the target device has microphone access for voice input functionality.
    *   Click the "Run" (▶️) button in Android Studio.

## Application Usage Guide

*   **Sending Text Messages:**
    1.  Type your message into the `OutlinedTextField` at the bottom of the screen.
    2.  Tap the "Send" `IconButton` (icon: `Icons.Filled.Send`).
*   **Sending Voice Messages:**
    1.  Tap the "Microphone" `IconButton` (icon: `Icons.Filled.Mic`).
    2.  The Android system's speech recognition prompt will appear. Speak your message.
    3.  The transcribed text will populate the input field.
    4.  Tap the "Send" `IconButton` to send the message.
*   The conversation, including user messages and AI responses, will be displayed in the `LazyColumn`.

## Key Code Components

*   **`MainActivity.kt`:**
    *   Manages Android Activity lifecycle.
    *   Handles runtime permissions for `RECORD_AUDIO`.
    *   Launches the `RecognizerIntent` for speech-to-text and processes the result.
    *   Sets up the main Composable UI (`ChatPage`).
*   **`ChatViewModel.kt`:**
    *   Extends `ViewModel` for lifecycle-aware data management.
    *   Holds the `messageList` (e.g., `MutableList<MessageModel>`) and `recognizedText` (`MutableStateFlow<String>`).
    *   Contains logic for `sendMessage()` to the AI service and `onSpeechResult()` to update the input field.
*   **`ChatPage.kt`:**
    *   The primary Composable function defining the chat screen layout.
    *   Uses `Scaffold` for overall structure (TopAppBar, message input area, content area).
    *   Employs `LazyColumn` to efficiently display the list of messages.
    *   Manages the state of the text input field (`TextFieldValue`).
    *   Includes `LaunchedEffect` for side-effects like scrolling to new messages.
*   **`MessageModel.kt`:**
    *   A data class (e.g., `data class MessageModel(val message: String, val role: String)`) representing a single message in the conversation, distinguishing between "user" and "model" roles.

---
This project serves as a practical example of building an AI-powered chat application on Android using modern development practices.

    
