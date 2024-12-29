package com.aymen.metastore.ui.screen.admin

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.InvoiceViewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.ClientDialog
import com.aymen.metastore.ui.component.InvoiceCard
import com.aymen.store.model.Enum.RoleEnum

//@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InvoiceScreenAsProvider() {
    val appViewModel: AppViewModel = hiltViewModel()
    val invoiceViewModel: InvoiceViewModel = hiltViewModel()
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val user by sharedViewModel.user.collectAsStateWithLifecycle()
    val listState = invoiceViewModel.listState
    val context = LocalContext.current
    var asProvider by remember {
        mutableStateOf(true)
    }
    val view by appViewModel.view
    appViewModel.updateView("ALL")
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp)
                ) {
                    ClientDialog(false, false) {
                        invoiceViewModel.invoiceMode = InvoiceMode.CREATE
                        appViewModel.updateShow("add invoice")
                    }
                }
                if (user.role != RoleEnum.WORKER) {

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp)
                ) {
                    ButtonSubmit(
                        labelValue = "get invoice as client",
                        color = Color.Green,
                        enabled = asProvider
                    ) {
                        asProvider = false
                        appViewModel.updateView("ALL")
                    }
                }

                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp)
                ) {
                    ButtonSubmit(
                        labelValue = "get invoice as provider",
                        color = Color.Green,
                        enabled = !asProvider
                    ) {
                        asProvider = true
                        appViewModel.updateView("ALL")
                    }

                }
            }
                    Row {
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                ButtonSubmit(
                                    labelValue = "all",
                                    color = Color.Green,
                                    enabled = true
                                ) {
                                    appViewModel.updateView("ALL")
                                }
                            }
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                ButtonSubmit(
                                    labelValue = "paid",
                                    color = Color.Green,
                                    enabled = true
                                ) {
                                    appViewModel.updateView("PAID")
                                }
                            }
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                ButtonSubmit(
                                    labelValue = "in complete",
                                    color = Color.Green,
                                    enabled = true
                                ) {

                                    appViewModel.updateView("IN_COMPLETE")
                                }
                            }
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                ButtonSubmit(
                                    labelValue = "not paid",
                                    color = Color.Green,
                                    enabled = true
                                ) {

                                    appViewModel.updateView("NOT_PAID")
                                }
                            }
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                ButtonSubmit(
                                    labelValue = "not accepted",
                                    color = Color.Green,
                                    enabled = true
                                ) {
                                    appViewModel.updateView("NOT_ACCEPTED")
                                    invoiceViewModel.getAllMyPaymentNotAccepted(asProvider)
                                }
                            }
                    }
                if(asProvider){
                            val invoicesAsProvider = invoiceViewModel.invoices.collectAsLazyPagingItems()
                    when(view){
                        "ALL" ->{
                            invoiceViewModel.setFilter(PaymentStatus.ALL)
                            LazyColumn(state = listState,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(count = invoicesAsProvider.itemCount,
                                    key = invoicesAsProvider.itemKey { it.id!! }
                                ) { index ->
                                    val invoice = invoicesAsProvider[index]
                                    if (invoice != null) {
                                        InvoiceCard(
                                            invoice,
                                            appViewModel,
                                            invoiceViewModel,
                                            asProvider
                                        )
                                    }
                                }
                            }
                        }
                        "PAID" ->{
                            invoiceViewModel.setFilter(PaymentStatus.PAID)
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(count = invoicesAsProvider.itemCount,
                                    key = { index -> invoicesAsProvider[index]?.id ?: index }
                                ) { index ->

                                    val invoice = invoicesAsProvider[index]
                                    if (invoice != null) {
                                        Row {
                                            Text(text = invoice.code.toString())
                                            Spacer(modifier = Modifier.padding(6.dp))
                                            Text(text = invoice.prix_invoice_tot.toString())
                                            Spacer(modifier = Modifier.padding(6.dp))
                                            Text(text = invoice.paid.toString())
                                            Spacer(modifier = Modifier.padding(6.dp))
                                        }
                                    }
                                }
                            }
                        }
                        "NOT_PAID" -> {
                            invoiceViewModel.setFilter(PaymentStatus.NOT_PAID)
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(count = invoicesAsProvider.itemCount,
                                    key = invoicesAsProvider.itemKey { it.id!! }
                                ) { index ->
                                    val invoice = invoicesAsProvider[index]
                                    if (invoice != null) {
                                        Row {
                                            Text(text = invoice.code.toString())
                                            Spacer(modifier = Modifier.padding(6.dp))
                                            Text(
                                                text = "client name :" + (invoice.client?.name
                                                    ?: invoice.person?.username)
                                            )
                                            Spacer(modifier = Modifier.padding(6.dp))
                                            Text(text = "invoice date :" + invoice.lastModifiedDate)
                                        }
                                    }
                                }
                            }
                        }
                        "IN_COMPLETE" ->{
                            invoiceViewModel.setFilter(PaymentStatus.INCOMPLETE)
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(count = invoicesAsProvider.itemCount,
                                    key = invoicesAsProvider.itemKey { it.id!! }
                                ) { index ->
                                    val invoice = invoicesAsProvider[index]
                                    if (invoice != null) {
                                        Row {
                                            Text(text = "invoice code : " + invoice.code.toString())
                                            Spacer(modifier = Modifier.padding(6.dp))
                                            Text(
                                                text = "client name : " + (invoice.person?.username
                                                    ?: invoice.client?.name!!)
                                            )
                                            Spacer(modifier = Modifier.padding(6.dp))
                                            Text(text = "rest is : " + invoice.rest.toString())
                                        }
                                    }
                                }
                            }
                        }
                        "NOT_ACCEPTED" ->{
                            val notAccepted = invoiceViewModel.notAcceptedAsProvider.collectAsLazyPagingItems()
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(count = notAccepted.itemCount,
                                    key = notAccepted.itemKey { it.id!! }
                                ) { index ->
                                    val invoice = notAccepted[index]
                                    if (invoice != null) {
                                        Row {
                                            Text(text = invoice.code.toString())
                                            Spacer(modifier = Modifier.padding(6.dp))
                                            Text(
                                                text = "client name :" + (invoice.client?.name
                                                    ?: invoice.person?.username)
                                            )
                                            Spacer(modifier = Modifier.padding(6.dp))
                                            Text(text = "invoice date :" + invoice.lastModifiedDate)
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
                else{
                            val invoiceAsClient = invoiceViewModel.invoicesAsClient.collectAsLazyPagingItems()
                    when(view){
                        "ALL" ->{
                            invoiceViewModel.setFilter(PaymentStatus.ALL)
                            LazyColumn {
                                items(count = invoiceAsClient.itemCount,
                                    key = invoiceAsClient.itemKey { it.id!! }
                                ) { index ->
                                    val invoice = invoiceAsClient[index]
                                    if (invoice != null) {
                                        InvoiceCard(invoice, appViewModel, invoiceViewModel, asProvider)
                                    }
                                }
                            }
                        }
                        "PAID" ->{
                            invoiceViewModel.setFilter(PaymentStatus.PAID)
                            LazyColumn {
                                items(count = invoiceAsClient.itemCount,
                                    key = invoiceAsClient.itemKey { it.id!! }
                                ) { index ->
                                    val invoice = invoiceAsClient[index]
                                    if (invoice != null) {
                                        InvoiceCard(invoice, appViewModel, invoiceViewModel, asProvider)
                                    }
                                }
                            }
                        }
                        "NOT_PAID" ->{
                            invoiceViewModel.setFilter(PaymentStatus.NOT_PAID)
                            LazyColumn {
                                items(count = invoiceAsClient.itemCount,
                                    key = invoiceAsClient.itemKey { it.id!! }
                                ) { index ->
                                    val invoice = invoiceAsClient[index]
                                    if (invoice != null) {
                                        InvoiceCard(invoice, appViewModel, invoiceViewModel, asProvider)
                                    }
                                }
                            }
                        }
                        "IN_COMPLETE" ->{
                            invoiceViewModel.setFilter(PaymentStatus.INCOMPLETE)
                            LazyColumn {
                                items(count = invoiceAsClient.itemCount,
                                    key = invoiceAsClient.itemKey { it.id!! }
                                ) { index ->
                                    val invoice = invoiceAsClient[index]
                                    if (invoice != null) {
                                        InvoiceCard(invoice, appViewModel, invoiceViewModel, asProvider)
                                    }
                                }
                            }
                        }
                        "NOT_ACCEPTED" ->{
                            val notAccepted = invoiceViewModel.notAcceptedAsClient.collectAsLazyPagingItems()
                            LazyColumn {
                                items(count = notAccepted.itemCount,
                                    key = notAccepted.itemKey { it.id!! }
                                ) { index ->
                                    val invoice = notAccepted[index]
                                    if (invoice != null) {
                                        InvoiceCard(invoice, appViewModel, invoiceViewModel, asProvider)
                                    }
                                }
                            }
                        }
                    }

                }
        }

    }
}

