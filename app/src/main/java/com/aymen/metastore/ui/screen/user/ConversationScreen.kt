package com.aymen.store.ui.screen.user

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.MessageViewModel
import com.aymen.store.ui.component.ConversationCard
import com.aymen.store.ui.screen.admin.MessageScreen

@Composable
fun ConversationScreen() {
    val context = LocalContext.current
    val messageViewModel : MessageViewModel = viewModel()
    val appViewModel : AppViewModel = viewModel()
        val show by appViewModel.show
    Toast.makeText(context, "$show", Toast.LENGTH_SHORT).show()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp)
    ) {
        when(show){
            "conversation" -> {
        ConversationCard(messageViewModel.myAllConversations)
        }
            "message" -> {
                MessageScreen()
            }
        }
    }
}