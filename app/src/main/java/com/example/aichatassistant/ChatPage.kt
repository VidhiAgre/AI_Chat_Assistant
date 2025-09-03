package com.example.aichatassistant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPage(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel,
    onVoiceInput: () -> Unit
) {
    var textFieldValueState by remember { mutableStateOf(TextFieldValue("")) }
    val messageList = chatViewModel.messageList
    val recognizedText by chatViewModel.recognizedText.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(recognizedText) {
        if (recognizedText.isNotEmpty()) {
            textFieldValueState = TextFieldValue(recognizedText)
            chatViewModel.clearRecognizedText()
        }
    }


    LaunchedEffect(messageList.size) {
        if (messageList.isNotEmpty()) {
            coroutineScope.launch {

                if (listState.layoutInfo.totalItemsCount > 0 && messageList.indices.contains(messageList.size - 1)) {
                    listState.animateScrollToItem(messageList.size - 1)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("AI Chat Assistant") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textFieldValueState,
                    onValueChange = { textFieldValueState = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type or speak a message...") },
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                // Voice Input Button
                IconButton(onClick = onVoiceInput) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "Speak",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Send Button
                IconButton(
                    onClick = {
                        if (textFieldValueState.text.isNotBlank()) {
                            chatViewModel.sendMessage(textFieldValueState.text)
                            textFieldValueState = TextFieldValue("")
                            keyboardController?.hide()
                        }
                    },
                    enabled = textFieldValueState.text.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Send",
                        tint = if (textFieldValueState.text.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messageList) { message ->
                MessageBubble(message = message)
            }
        }
    }
}

@Composable
fun MessageBubble(message: MessageModel) {
    val isUserMessage = message.role == "user"
    val bubbleColor = if (isUserMessage) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val textColor = if (isUserMessage) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
    val horizontalAlignment = if (isUserMessage) Alignment.End else Alignment.Start

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(horizontalAlignment)
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = if (isUserMessage) 16.dp else 0.dp,
                        topEnd = if (isUserMessage) 0.dp else 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
                .background(bubbleColor)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = message.message,
                color = textColor,
                fontSize = 16.sp
            )
        }
    }
}