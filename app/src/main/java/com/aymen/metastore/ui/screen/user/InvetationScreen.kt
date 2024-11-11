package com.aymen.store.ui.screen.user

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aymen.store.model.repository.ViewModel.InvetationViewModel
import com.aymen.store.ui.component.InvetationCard

@Composable
fun InvetationScreen(modifier: Modifier = Modifier) {
    val invetationViewModel : InvetationViewModel = hiltViewModel()
    val invitations by invetationViewModel.myAllInvetation.collectAsStateWithLifecycle()

LaunchedEffect(key1 = Unit) {
    invetationViewModel.getAllMyInvetations()
}

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(invitations){
                InvetationCard(it){status->
                    invetationViewModel.RequestResponse(status,it.invitation.id!!)
                }
            }
        }
    }
}