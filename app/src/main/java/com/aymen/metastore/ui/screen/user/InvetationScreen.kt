package com.aymen.store.ui.screen.user

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.store.model.repository.ViewModel.InvetationViewModel
import com.aymen.store.ui.component.InvetationCard

@Composable
fun InvetationScreen(modifier: Modifier = Modifier) {
    val invetationViewModel : InvetationViewModel = viewModel()
LaunchedEffect(key1 = Unit) {
    invetationViewModel.getAllMyInvetations()
}

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(invetationViewModel.myAllInvetation){
                InvetationCard(it){status->
                    invetationViewModel.RequestResponse(status,it.id!!)
                }
            }
        }
    }
}