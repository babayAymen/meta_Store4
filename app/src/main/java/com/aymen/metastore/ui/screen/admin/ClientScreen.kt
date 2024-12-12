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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.dependencyInjection.BASE_URL
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
            items(count = myClients.itemCount,
                key = myClients.itemKey { it.id!! }) { index ->
                val client = myClients[index]
                if (client != null) {
                    Column {
                        SwipeToDeleteContainer(
                            client,
                            onDelete = {item ->
                                Log.e("aymenbabatdelete", "delete")
                            },
                            onUpdate = {item ->
                                clientViewModel.assignClientForUpdate(item.client!!)
                                appViewModel.updateShow("add client")

                            }
                        ) { client ->
                            ClientCard(
                                client,
                                image = if (client.client != null) {
                                    "${BASE_URL}werehouse/image/${client.client.logo}/company/${if (client.client.virtual == true) client.provider?.user?.id else client.client.user?.id}"
                                } else "${BASE_URL}werehouse/image/${client.person?.image}/user/${client.person?.id}"
                            )
                        }

                    }
                }
            }
        }
    }
}