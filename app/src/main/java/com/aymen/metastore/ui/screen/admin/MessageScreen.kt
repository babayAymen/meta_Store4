package com.aymen.store.ui.screen.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.entity.realm.Conversation
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.MessageViewModel
import com.aymen.store.ui.component.InputTextField
import com.aymen.store.ui.component.MessageCard
import okhttp3.internal.wait

@Composable
fun MessageScreen() {
    val messageViewModel : MessageViewModel = viewModel()
    val appViewModel : AppViewModel = viewModel()
    var message by remember {
        mutableStateOf("")
    }
    LaunchedEffect(key1 = Unit) {
        messageViewModel.getMyName()
    }
    val messageHeight = LocalConfiguration.current.screenHeightDp.dp
    DisposableEffect(Unit) {
        onDispose {
            appViewModel.updateShow("conversation")
            messageViewModel.myAllMessages = emptyList()
            messageViewModel.conversation = Conversation()
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp)
            .background(color = Color.White)
    ) {

        Box(
           modifier = Modifier.fillMaxSize()
        ) {


            Column {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .weight(0.88f),
                ) {
                        MessageCard(message = messageViewModel.myAllMessages)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Start)
                        .background(color = Color.White)
                        .heightIn(min = messageHeight / 10)
                ) {
                    Column {

                        InputTextField(
                            labelValue = message,
                            label = "Type a message",
                            singleLine = false,
                            maxLine = 6,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Send
                            ),
                            onValueChange = {
                                message = it
                            }
                            , onImage = {}
                        ) {
                            messageViewModel.sendMessage.content = message
                            messageViewModel.sendMessage(message = message)
                            message = ""
                        }
                    }
                }
            }
        }

    }
}

