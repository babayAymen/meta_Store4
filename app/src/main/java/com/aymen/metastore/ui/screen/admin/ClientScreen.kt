package com.aymen.store.ui.screen.admin

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.store.dependencyInjection.BASE_URL
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.ClientViewModel
import com.aymen.store.ui.component.ButtonSubmit
import com.aymen.store.ui.component.ClientCard

@Composable
fun ClientScreen() {
    val appViewModel : AppViewModel = viewModel()
    val clientViewModel : ClientViewModel = viewModel()
    LaunchedEffect(key1 = true) {
        clientViewModel.getAllMyClient()
    }

    val clients by clientViewModel.myClients.collectAsState(initial = emptyList())
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {

            Row (
                modifier = Modifier.fillMaxWidth()
            ){

                ButtonSubmit(labelValue = "Add New Client", color = Color.Green, enabled = true) {
                    appViewModel.updateShow("add client")
                }
            }
            Row (
                modifier = Modifier.fillMaxWidth()
            ){
                Column {
                    clients.forEach{
                        SwipeToDeleteContainer(
                            it,
                            onDelete = {
                                Log.e("aymenbabatdelete","delete")
                            },
                            appViewModel = appViewModel,
                        ){client ->
                            ClientCard(client,
                                image = if(client.client != null) "${BASE_URL}werehouse/image/"
                                        + client.client?.logo+"/company/"
                                        +if(client.client?.virtual!!) {client.provider?.user?.id} else
                                        client.client?.user?.id else "${BASE_URL}werehouse/image/"
                                + client.person?.image+"/user/"+client.person?.id
                            )
                        }

                    }
                }
            }
            }

        }
    }
}