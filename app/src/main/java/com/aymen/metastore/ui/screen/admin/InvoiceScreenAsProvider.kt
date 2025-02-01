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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.R
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.InvoiceViewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.ClientDialog
import com.aymen.metastore.ui.component.InvoiceCard
import com.aymen.metastore.ui.component.ProviderDialog
import com.aymen.metastore.util.ADD_INVOICE
import com.aymen.metastore.util.ALL
import com.aymen.metastore.util.IN_COMPLETE
import com.aymen.metastore.util.NOT_ACCEPTED
import com.aymen.metastore.util.NOT_PAID
import com.aymen.metastore.util.PAID
import com.aymen.store.model.Enum.RoleEnum

@Composable
fun InvoiceScreenAsProvider(asClient : Boolean) {
    val appViewModel: AppViewModel = hiltViewModel()
    val invoiceViewModel: InvoiceViewModel = hiltViewModel()
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val user by sharedViewModel.user.collectAsStateWithLifecycle()
    val listState = invoiceViewModel.listState
    val context = LocalContext.current
    var asProvider by remember {
        mutableStateOf(!asClient)
    }
    val view by appViewModel.view
    var launchLaucheEffect by remember {
        mutableStateOf("ALL")
    }
    var enabledAll by remember {
        mutableStateOf(false)
    }
    var enabledPaid by remember {
        mutableStateOf(true)
    }
    var enabledIncomplete by remember {
        mutableStateOf(true)
    }
    var enabledNotPaid by remember {
        mutableStateOf(true)
    }
    var enabledNotAccepted by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(key1 = launchLaucheEffect, key2 = asProvider) {
        Log.e("azertyhgfdss","launcgh : $launchLaucheEffect , asprovider : $asProvider")
        when(launchLaucheEffect){
            ALL -> appViewModel.updateView(ALL)
            PAID -> appViewModel.updateView(PAID)
            NOT_PAID -> appViewModel.updateView(NOT_PAID)
            IN_COMPLETE -> appViewModel.updateView(IN_COMPLETE)
            NOT_ACCEPTED -> appViewModel.updateView(NOT_ACCEPTED)
        }
    }
    LaunchedEffect(key1 = view) {
        when(view){
            ALL ->{
                enabledAll = false
                enabledPaid = true
                enabledIncomplete = true
                enabledNotPaid = true
                enabledNotAccepted = true
            }
            PAID ->{
                enabledAll = true
                enabledPaid = false
                enabledIncomplete = true
                enabledNotPaid = true
                enabledNotAccepted = true
                }
            NOT_PAID ->{
                enabledAll = true
                enabledPaid = true
                enabledIncomplete = true
                enabledNotPaid = false
                enabledNotAccepted = true
            }
            IN_COMPLETE ->{
                enabledAll = true
                enabledPaid = true
                enabledIncomplete = false
                enabledNotPaid = true
                enabledNotAccepted = true
            }
            NOT_ACCEPTED ->{
                enabledAll = true
                enabledPaid = true
                enabledIncomplete = true
                enabledNotPaid = true
                enabledNotAccepted = false
            }
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (user.role != RoleEnum.WORKER) {

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                    ) {
                        ButtonSubmit(
                            labelValue = stringResource(id = R.string.get_invoice_as_client),
                            color = Color.Green,
                            enabled = asProvider
                        ) {
                            asProvider = false
                        }
                    }

                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp)
                ) {
                    ButtonSubmit(
                        labelValue = stringResource(id = R.string.get_invoice_as_provider),
                        color = Color.Green,
                        enabled = !asProvider
                    ) {
                        asProvider = true
                    }

                }
            }
                    Row {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                        ) {if(asProvider)
                            ClientDialog(false, false) {
                                if(it) {
                                    invoiceViewModel.asProvider = true
                                    invoiceViewModel.setInvoiceMode(InvoiceMode.CREATE)
                                    appViewModel.updateShow(ADD_INVOICE)
                                }
                            }else
                            ProviderDialog(false, false) {
                                if(it) {
                                    invoiceViewModel.asProvider = false
                                    invoiceViewModel.setInvoiceMode(InvoiceMode.CREATE)
                                    appViewModel.updateShow(ADD_INVOICE)
                                }
                            }
                        }
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                ButtonSubmit(
                                    labelValue = stringResource(id = R.string.all),
                                    color = Color.Green,
                                    enabled = enabledAll
                                ) {
                                    launchLaucheEffect = ALL
                                }
                            }
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                ButtonSubmit(
                                    labelValue = stringResource(id = R.string.paid),
                                    color = Color.Green,
                                    enabled = enabledPaid
                                ) {
                                    launchLaucheEffect = PAID
                                }
                            }
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                ButtonSubmit(
                                    labelValue = stringResource(id = R.string.in_complete),
                                    color = Color.Green,
                                    enabled = enabledIncomplete
                                ) {
                                    launchLaucheEffect = IN_COMPLETE
                                }
                            }
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                ButtonSubmit(
                                    labelValue = stringResource(id = R.string.not_paid),
                                    color = Color.Green,
                                    enabled = enabledNotPaid
                                ) {
                                    launchLaucheEffect = NOT_PAID
                                }
                            }
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                ButtonSubmit(
                                    labelValue = stringResource(id = R.string.not_accepted),
                                    color = Color.Green,
                                    enabled = enabledNotAccepted
                                ) {
                                    launchLaucheEffect = NOT_ACCEPTED
                                    invoiceViewModel.getAllMyPaymentNotAccepted(asProvider)
                                }
                            }
                    }
                if(asProvider){
                            val invoicesAsProvider = invoiceViewModel.invoices.collectAsLazyPagingItems()
                            sharedViewModel.setInvoiceCountNotification(true)
                    when(view){
                        ALL ->{
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
                        PAID ->{
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
                        NOT_PAID -> {
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
                                                text = stringResource(id = R.string.client_name,invoice.client?.name
                                                    ?: invoice.person?.username?:"")
                                            )
                                            Spacer(modifier = Modifier.padding(6.dp))
                                            Text(text = stringResource(id = R.string.invoice_date,invoice.lastModifiedDate?:""))
                                        }
                                    }
                                }
                            }
                        }
                        IN_COMPLETE ->{
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
                                            Text(text =  stringResource(id = R.string.invoice_code, invoice.code.toString()))
                                            Spacer(modifier = Modifier.padding(6.dp))
                                            Text(
                                                text = stringResource(id = R.string.client_name,invoice.person?.username
                                                    ?: invoice.client?.name!!)
                                            )
                                            Spacer(modifier = Modifier.padding(6.dp))
                                            Text(text = stringResource(id = R.string.rest_is, invoice.rest.toString()))
                                        }
                                    }
                                }
                            }
                        }
                        NOT_ACCEPTED ->{
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
                                                text = stringResource(id = R.string.client_name,invoice.client?.name
                                                    ?: invoice.person?.username?:""))
                                            Spacer(modifier = Modifier.padding(6.dp))
                                            Text(text = stringResource(id = R.string.invoice_date,invoice.lastModifiedDate?:""))
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
                else{
                            val invoiceAsClient = invoiceViewModel.invoicesAsClient.collectAsLazyPagingItems()
                            sharedViewModel.setInvoiceAsClientCountNotification(true)
                    when(view){
                        ALL ->{

                            Log.e("viewmodelldn","view in screen is : $view")
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
                        PAID ->{
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
                        NOT_PAID ->{
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
                        IN_COMPLETE ->{
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
                        NOT_ACCEPTED ->{
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

