package com.aymen.metastore.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.aymen.metastore.model.entity.model.Conversation
import com.aymen.metastore.model.entity.model.Message
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.MessageViewModel
import com.aymen.metastore.ui.component.InputTextField
import com.aymen.metastore.ui.component.MessageCard

@Composable
fun MessageScreen() {
    val messageViewModel: MessageViewModel = hiltViewModel()
    val appViewModel: AppViewModel = hiltViewModel()
    var message by remember { mutableStateOf("") }

    if(messageViewModel.fromConve) {
//            messageViewModel.getAllMyMessageByConversationId()
    }

    val allMyMessage = messageViewModel.myAllMessages.collectAsLazyPagingItems()

    DisposableEffect(Unit) {
        onDispose {
            messageViewModel.sendMessage = Message()
            messageViewModel.conversation = Conversation()
            messageViewModel.fromConve = false
           }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Message list
            Row(
                modifier = Modifier
                    .weight(9f)
                    .fillMaxWidth()
            ) {
                MessageCard(message = allMyMessage)
            }
            Row(
                modifier = Modifier
                    .weight(1f) // Adjust weight based on keyboard visibility
                    .fillMaxWidth() // Ensure it fills the width
            ) {
                InputTextField(
                    labelValue = message,
                    label = "Type a message",
                    singleLine = false,
                    maxLine = 6, // Allow multiple lines
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Send
                    ),
                    onValueChange = {
                        message = it
                    },
                    onImage = {},
                    onImeAction = { file ->
                        // Handle the send action here
                        messageViewModel.sendMessage.content = message
                        messageViewModel.sendMessage(message = message)
                        message = ""
                    }
                )
            }
        }
    }
}



