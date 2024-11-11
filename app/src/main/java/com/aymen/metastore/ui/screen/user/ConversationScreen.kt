package com.aymen.store.ui.screen.user

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.MessageViewModel
import com.aymen.store.ui.component.ConversationCard
import com.aymen.store.ui.screen.admin.MessageScreen

@Composable
fun ConversationScreen() {
    val context = LocalContext.current
    val messageViewModel : MessageViewModel = hiltViewModel()
    val appViewModel : AppViewModel = hiltViewModel()
    val show by appViewModel.show
    LaunchedEffect(key1 = Unit) {
        messageViewModel.getAllMyConversation()
    }
    val myAllConvDto by messageViewModel.myAllConversationsDto.collectAsStateWithLifecycle()
    Toast.makeText(context, "$show", Toast.LENGTH_SHORT).show()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp)
    ) {
        when(show){
            "conversation" -> {
                Log.e("conversationscreen","conv dto size : ${myAllConvDto.size}")
        ConversationCard(myAllConvDto)
        }
            "message" -> {
                MessageScreen()
            }
        }
    }
}