package com.aymen.metastore.ui.screen.user

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.MessageViewModel
import com.aymen.metastore.ui.component.ConversationCard
import com.aymen.metastore.ui.screen.admin.MessageScreen

@Composable
fun ConversationScreen() {
    val context = LocalContext.current
    val messageViewModel : MessageViewModel = hiltViewModel()
    val appViewModel : AppViewModel = hiltViewModel()
    val show by appViewModel.show
    val myAllConv = messageViewModel.myAllConversations.collectAsLazyPagingItems()
    Toast.makeText(context, "$show", Toast.LENGTH_SHORT).show()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp)
    ) {
        when(show){
            "conversation" -> {
        ConversationCard(myAllConv)
        }
            "message" -> {
                MessageScreen()
            }
        }
    }
}