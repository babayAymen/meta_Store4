package com.aymen.metastore.ui.screen.admin

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.InvoiceViewModel
import com.aymen.store.model.repository.ViewModel.OrderViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.InvoiceCard
import com.aymen.metastore.ui.component.PuchaseOrderCard
import com.aymen.store.model.Enum.AccountType

@Composable
fun OrderScreen( invoiceViewModel: InvoiceViewModel, appViewModel: AppViewModel) {
    val invoicesIDelivered = invoiceViewModel.invoicesIDelivered.collectAsLazyPagingItems()
    val invoicesDontDelivered = invoiceViewModel.invoicesNotDelivered.collectAsLazyPagingItems()
    val view by appViewModel.view
    var isDelivered by remember {
        mutableStateOf(false)
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
            ) {
                LazyColumn {
                    item {
                        Row {
                            Row (
                                modifier = Modifier.weight(1f)
                            ){
                                ButtonSubmit(
                                    labelValue = "delivered",
                                    color = Color.Green,
                                    enabled = !isDelivered
                                ) {
                                    if(invoicesIDelivered.itemCount == 0) {
                                        invoiceViewModel.getInvoicesIDelivered()
                                    }
                                    isDelivered = !isDelivered
                                    appViewModel.updateView("DELIVERED")
                                }
                            }
                            Row (
                                modifier = Modifier.weight(1f)
                            ){

                                ButtonSubmit(
                                    labelValue = "still not delivered",
                                    color = Color.Green,
                                    enabled = isDelivered
                                ) {
                                    isDelivered = !isDelivered
                                    appViewModel.updateView("NOT_DELIVERED")
                                }
                            }
                        }
                    }
                    when (view) {
                        "NOT_DELIVERED" -> {
                            items(count = invoicesDontDelivered.itemCount,
                                key = invoicesDontDelivered.itemKey { it.id!! }) { index ->
                                val order = invoicesDontDelivered[index]
                                if (order != null) {
                                    PuchaseOrderCard(
                                        order = order,
                                        appViewModel = appViewModel,
                                        invoiceViewModel = invoiceViewModel,
                                        asProvider = false
                                    )
                                }
                            }
                        }
                        "DELIVERED" -> {
                            items(count = invoicesIDelivered.itemCount,
                                key = invoicesIDelivered.itemKey { it.id!! }) { index ->
                                val order = invoicesIDelivered[index]
                                if (order != null) {
                                    PuchaseOrderCard(
                                        order = order,
                                        appViewModel = appViewModel,
                                        invoiceViewModel = invoiceViewModel,
                                        asProvider = false
                                    )
                                }
                            }
                        }
                    }

                }
            }

        }
    }

}