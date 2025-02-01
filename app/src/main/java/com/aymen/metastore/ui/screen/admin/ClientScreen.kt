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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.R
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.ClientViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.ClientCard
import com.aymen.metastore.util.ADD_CLIENT
import com.aymen.metastore.util.BASE_URL
import com.aymen.metastore.util.IMAGE_URL_COMPANY
import com.aymen.metastore.util.IMAGE_URL_USER

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
                        labelValue = stringResource(id = R.string.add_new_client),
                        color = Color.Green,
                        enabled = true
                    ) {
                        appViewModel.updateShow(ADD_CLIENT)
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
                                clientViewModel.deleteClient(item)
                            },
                            onUpdate = {item ->
                                clientViewModel.assignClientForUpdate(item.client!!)
                                appViewModel.updateShow(ADD_CLIENT)

                            }
                        ) { client ->
                            ClientCard(
                                client,
                                image = if (client.client != null) {
                                    String.format(IMAGE_URL_COMPANY,client.client.logo,if (client.client.virtual == true) client.provider?.user?.id else client.client.user?.id)
                                } else String.format(IMAGE_URL_USER,client.person?.image,client.person?.id)
                            )
                        }

                    }
                }
            }
        }
    }
}