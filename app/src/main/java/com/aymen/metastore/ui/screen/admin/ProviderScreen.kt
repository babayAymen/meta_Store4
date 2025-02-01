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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.R
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.ProviderViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.NotImage
import com.aymen.metastore.ui.component.ProviderCard
import com.aymen.metastore.util.ADD_PROVIDER
import com.aymen.metastore.util.BASE_URL
import com.aymen.metastore.util.IMAGE_URL_COMPANY

@Composable
fun ProviderScreen() {
    val appViewModel : AppViewModel = hiltViewModel()
    val providerViewModel : ProviderViewModel = hiltViewModel()
    LaunchedEffect(key1 = Unit) {
        providerViewModel.isAll = true
    }
    val providers = providerViewModel.providers.collectAsLazyPagingItems()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row (
                modifier = Modifier.fillMaxWidth()
            ){

                ButtonSubmit(labelValue = stringResource(id = R.string.add_new_provider), color = Color.Green, enabled = true) {
                    appViewModel.updateShow(ADD_PROVIDER)
                }
            }
               LazyColumn {
                   items(
                       count = providers.itemCount,
                       key = providers.itemKey { it.id!! }) { index ->
                       val provider = providers[index]
                       if (provider != null) {
                           SwipeToDeleteContainer(
                               provider,
                               onDelete = {item ->
                                   providerViewModel.deleteProvider(item)
                               },
                               onUpdate = {item ->
                                   providerViewModel.associateProviderForUpdate(item.provider!!)
                                   providerViewModel.update = true
                                   appViewModel.updateShow(ADD_PROVIDER)

                               }
                           ) { prvd ->
                               ProviderCard(
                                   prvd,
                                   image = String.format(IMAGE_URL_COMPANY,provider.provider?.logo,
                                       if (provider.provider?.virtual == true) provider.client?.user?.id else provider.provider?.user?.id)


                               )
                           }

                       }
                   }
               }
        }
    }
}