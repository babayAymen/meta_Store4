package com.aymen.metastore.ui.screen.admin

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.InvoiceViewModel
import com.aymen.metastore.model.repository.ViewModel.PaymentViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.ClientDialog
import com.aymen.metastore.ui.component.InvoiceCard

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InvoiceScreenAsProvider() {
    val appViewModel: AppViewModel = hiltViewModel()
    val invoiceViewModel: InvoiceViewModel = hiltViewModel()
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
                                    invoiceViewModel.getAllMyPaymentFromInvoicee(PaymentStatus.ALL,asProvider)
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
                                    invoiceViewModel.getAllMyPaymentFromInvoicee(PaymentStatus.PAID,asProvider)
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
                                    invoiceViewModel.getAllMyPaymentFromInvoicee(PaymentStatus.INCOMPLETE,asProvider)
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
                                    invoiceViewModel.getAllMyPaymentFromInvoicee(PaymentStatus.NOT_PAID,asProvider)
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
                    when(view){
                        "ALL" ->{
                            val invoicesAsProvider = invoiceViewModel.myInvoicesAsProvider.collectAsLazyPagingItems()
                            LazyColumn(
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
                            val paid = invoiceViewModel.paid.collectAsLazyPagingItems()
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(count = paid.itemCount,
                                    key = paid.itemKey { it.id!! }
                                ) { index ->
                                    val invoice = paid[index]
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
                            val notPaid = invoiceViewModel.notPaid.collectAsLazyPagingItems()
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(count = notPaid.itemCount,
                                    key = notPaid.itemKey { it.id!! }
                                ) { index ->
                                    val invoice = notPaid[index]
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
                            val inComplete = invoiceViewModel.inComplete.collectAsLazyPagingItems()
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(count = inComplete.itemCount,
                                    key = inComplete.itemKey { it.id!! }
                                ) { index ->
                                    val invoice = inComplete[index]
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
                            val notAccepted = invoiceViewModel.notAccepted.collectAsLazyPagingItems()
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
                    when(view){
                        "ALL" ->{
                            val invoiceAsClient = invoiceViewModel.myInvoicesAsClient.collectAsLazyPagingItems()
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
                            val paid = invoiceViewModel.paid.collectAsLazyPagingItems()
                            LazyColumn {
                                items(count = paid.itemCount,
                                    key = paid.itemKey { it.id!! }
                                ) { index ->
                                    val invoice = paid[index]
                                    if (invoice != null) {
                                        InvoiceCard(invoice, appViewModel, invoiceViewModel, asProvider)
                                    }
                                }
                            }
                        }
                        "NOT_PAID" ->{
                            val notPaid = invoiceViewModel.notPaid.collectAsLazyPagingItems()
                            LazyColumn {
                                items(count = notPaid.itemCount,
                                    key = notPaid.itemKey { it.id!! }
                                ) { index ->
                                    val invoice = notPaid[index]
                                    if (invoice != null) {
                                        InvoiceCard(invoice, appViewModel, invoiceViewModel, asProvider)
                                    }
                                }
                            }
                        }
                        "IN_COMPLETE" ->{
                            val inComplete = invoiceViewModel.inComplete.collectAsLazyPagingItems()
                            LazyColumn {
                                items(count = inComplete.itemCount,
                                    key = inComplete.itemKey { it.id!! }
                                ) { index ->
                                    val invoice = inComplete[index]
                                    if (invoice != null) {
                                        InvoiceCard(invoice, appViewModel, invoiceViewModel, asProvider)
                                    }
                                }
                            }
                        }
                        "NOT_ACCEPTED" ->{
                            val notAccepted = invoiceViewModel.notAccepted.collectAsLazyPagingItems()
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

