package com.aymen.store.ui.screen.admin

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.store.dependencyInjection.BASE_URL
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.ProviderViewModel
import com.aymen.store.ui.component.ButtonSubmit
import com.aymen.store.ui.component.ProviderCard

@Composable
fun ProviderScreen() {
    val appViewModel : AppViewModel = viewModel()
    val providerViewModel : ProviderViewModel = viewModel()
    LaunchedEffect(key1 = true) {
        providerViewModel.getAllMyProviders()
    }
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

                ButtonSubmit(labelValue = "Add New Provider", color = Color.Green, enabled = true) {
                    appViewModel.updateShow("add provider")
                }
            }
            Row (
                modifier = Modifier.fillMaxWidth()
            ){
                Column {
                    providerViewModel.providers.forEach{
                        SwipeToDeleteContainer(
                            it,
                            onDelete = {
                                Log.e("aymenbabatdelete","delete")
                            },
                            appViewModel = appViewModel
                        ){provider ->
                            ProviderCard(it,
                                image = "${BASE_URL}werehouse/image/${provider.provider?.logo}/company/"+ if(provider.provider?.virtual == true){provider.client?.user?.id} else {provider.provider?.user?.id}
                            )
                        }

                    }
                }
            }

        }
    }
}