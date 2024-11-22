package com.aymen.metastore.ui.screen.admin

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.aymen.store.dependencyInjection.BASE_URL
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.ClientViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.ClientCard

@Composable
fun ClientScreen() {
    val appViewModel = hiltViewModel<AppViewModel>()
    val clientViewModel = hiltViewModel<ClientViewModel>()
    val myClients = clientViewModel.myClients.collectAsLazyPagingItems()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    ButtonSubmit(
                        labelValue = "Add New Client",
                        color = Color.Green,
                        enabled = true
                    ) {
                        appViewModel.updateShow("add client")
                    }
                }
            }
            items(myClients.itemCount) { index ->
                val client = myClients[index]
                Log.e("clienttest","client : $client")
                if (client != null) {
                    Column {
                        SwipeToDeleteContainer(
                            client,
                            onDelete = {
                                Log.e("aymenbabatdelete", "delete")
                            },
                            appViewModel = appViewModel,
                        ) { client ->
                            ClientCard(
                                client,
                                image = if (client.client != null) {
                                    "${BASE_URL}werehouse/image/${client.client.logo}/company/"
                                    if (client.client.virtual!!) "${client.provider?.user?.id}" else ""
                                } else "${BASE_URL}werehouse/image/${client.person?.image}/user/${client.person?.id}"
                            )
                        }

                    }
                }
            }
        }
    }
}