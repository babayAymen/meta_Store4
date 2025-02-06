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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.R
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.InvoiceViewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.repository.ViewModel.OrderViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.InvoiceCard
import com.aymen.metastore.ui.component.PuchaseOrderCard
import com.aymen.metastore.util.DELIVERED
import com.aymen.metastore.util.MY_NOT_DELIVERED
import com.aymen.metastore.util.NOT_DELIVERED
import com.aymen.store.model.Enum.AccountType

@Composable
fun OrderScreen( invoiceViewModel: InvoiceViewModel, appViewModel: AppViewModel, sharedViewModel: SharedViewModel) {
    val invoicesIDelivered = invoiceViewModel.invoicesIDelivered.collectAsLazyPagingItems()
    val invoicesDontDelivered = invoiceViewModel.invoicesNotDelivered.collectAsLazyPagingItems()
    val user by sharedViewModel.user.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val view by appViewModel.view
    var deliveredEnabled by remember {
        mutableStateOf(true)
    }
    var notDeliveredEnabled by remember {
        mutableStateOf(false)
    }
    var myNotDeliveredEnabled by remember {
        mutableStateOf(true)
    }
   when(view){
       "MY_NOT_DELIVERED" -> {
           if(invoicesIDelivered.itemCount == 0) {
               invoiceViewModel.getInvoicesIDelivered()
           }
           myNotDeliveredEnabled = false
           deliveredEnabled = true
           notDeliveredEnabled = true
       }
       "DELIVERED" -> {
           if(invoicesIDelivered.itemCount == 0) {
               invoiceViewModel.getInvoicesIDelivered()
           }
           myNotDeliveredEnabled = true
           deliveredEnabled = false
           notDeliveredEnabled = true
       }
       "NOT_DELIVERED" -> {
           myNotDeliveredEnabled = true
           deliveredEnabled = true
           notDeliveredEnabled = false
       }
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
                                    labelValue = stringResource(id = R.string.delivered),
                                    color = Color.Green,
                                    enabled = deliveredEnabled
                                ) {
                                    appViewModel.updateView(
                                        DELIVERED
                                    )
                                }
                            }
                            Row (
                                modifier = Modifier.weight(1f)
                            ){

                                ButtonSubmit(
                                    labelValue = stringResource(id = R.string.still_not_delivered),
                                    color = Color.Green,
                                    enabled = notDeliveredEnabled
                                ) {
                                    appViewModel.updateView(NOT_DELIVERED)
                                }
                            }
                            Row (
                                modifier = Modifier.weight(1f)
                            ){

                                ButtonSubmit(
                                    labelValue = stringResource(id = R.string.takin_still_not_delivered),
                                    color = Color.Green,
                                    enabled = myNotDeliveredEnabled
                                ) {
                                    appViewModel.updateView(MY_NOT_DELIVERED)
                                }
                            }
                        }
                    }
                    when (view) {
                        NOT_DELIVERED -> {
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
                        DELIVERED -> {
                            items(count = invoicesIDelivered.itemCount,
                                key = invoicesIDelivered.itemKey { it.id!! }) { index ->
                                val order = invoicesIDelivered[index]
                                if (order != null && order.isDelivered == true) {
                                    PuchaseOrderCard(
                                        order = order,
                                        appViewModel = appViewModel,
                                        invoiceViewModel = invoiceViewModel,
                                        asProvider = false
                                    )
                                }
                            }
                        }
                        MY_NOT_DELIVERED -> {
                            items(count = invoicesIDelivered.itemCount,
                                key = invoicesIDelivered.itemKey { it.id!! }) { index ->
                                val order = invoicesIDelivered[index]
                                if (order != null && order.isDelivered == false) {
                                    invoiceViewModel.setMyOrdersNotDelivered(order)
                                    PuchaseOrderCard(
                                        order = order,
                                        appViewModel = appViewModel,
                                        invoiceViewModel = invoiceViewModel,
                                        asProvider = false
                                    )
                                    ButtonSubmit(
                                        labelValue = "tract",
                                        color = Color.Green,
                                        enabled = true
                                    ) {
                                //       invoiceViewModel.navigateToGoogleMaps(context = context , order.client?.latitude?:order.person?.latitude!!, order.client?.longitude?:order.person?.longitude!!)
                                       invoiceViewModel.navigateOptimizedRoute(context = context , user.latitude!!, user.longitude!!)
                                    }
                                }
                            }
                        }
                    }

                }
            }

        }
    }

}