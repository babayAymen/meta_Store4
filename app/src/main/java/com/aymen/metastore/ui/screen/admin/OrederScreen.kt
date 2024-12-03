package com.aymen.metastore.ui.screen.admin

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.OrderViewModel
import com.aymen.metastore.ui.component.ButtonSubmit

@Composable
fun OrderScreen() {
    val appViewModel : AppViewModel = viewModel()
    val orderViewModel : OrderViewModel = viewModel()

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

                ButtonSubmit(labelValue = "get as client", color = Color.Green, enabled = true) {
                    appViewModel.updateShow("")
                }
            }
            Row (
                modifier = Modifier.fillMaxWidth()
            ){
                Column {
                    orderViewModel.orders.forEach{
                        SwipeToDeleteContainer(
                            it,
                            onDelete = {
                                Log.e("aymenbabatdelete","delete")
                            },
                            onUpdate = {
                                Log.e("aymenbabatdelete", "delete")

                            }
                        ){
                            //OrderCard(it)
                        }

                    }
                }
            }

        }
    }

}