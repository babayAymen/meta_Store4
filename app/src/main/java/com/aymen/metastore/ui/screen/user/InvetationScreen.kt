package com.aymen.metastore.ui.screen.user

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.aymen.metastore.model.repository.ViewModel.InvetationViewModel
import com.aymen.metastore.ui.component.InvetationCard

@Composable
fun InvetationScreen(modifier: Modifier = Modifier) {
    val invetationViewModel : InvetationViewModel = hiltViewModel()
    val invitations = invetationViewModel.myAllInvetation.collectAsLazyPagingItems()

LaunchedEffect(key1 = Unit) {
    invetationViewModel.getAllMyInvetations()
}

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(invitations.itemCount){index ->
                val invitation = invitations[index]
                InvetationCard(invitation!!){status->
                    invetationViewModel.RequestResponse(status,invitation.id!!)
                }
            }
        }
    }
}