package com.aymen.store.ui.screen.admin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.entity.realm.Invoice
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.InvoiceViewModel
import com.aymen.store.ui.component.ButtonSubmit
import com.aymen.store.ui.component.ClientDialog
import com.aymen.store.ui.component.InvoiceCard

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InvoiceScreenAsProvider() {
    val appViewModel : AppViewModel = hiltViewModel()
    val invoiceViewModel : InvoiceViewModel = hiltViewModel()
    var asProvider by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(asProvider) {
        if (asProvider) {
            invoiceViewModel.getAllMyInvoicesAsProvider()
        } else {
            invoiceViewModel.getAllMyInvoicesAsClient()
        }
    }
    val invoicesAsProvider = if (asProvider) {
        invoiceViewModel.myInvoicesAsProvider.collectAsStateWithLifecycle().value
    } else {
        invoiceViewModel.myInvoicesAsClient.collectAsStateWithLifecycle().value
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
                        invoiceViewModel.isLoading = true
                        asProvider = false
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
                        invoiceViewModel.isLoading = true
                        asProvider = true
                    }

                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (invoiceViewModel.isLoading) {
                    item {

                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                } else {
                    item {
                        Row {

                    if(asProvider){
                        Row (
                            modifier = Modifier.weight(1f)
                        ){
                        ButtonSubmit(
                            labelValue = "paid",
                            color = Color.Green,
                            enabled = true
                        ) {
                            invoiceViewModel.getAllMyInvoicesAsProviderAndStatus( PaymentStatus.PAID)
                        }
                        }
                        Row (
                            modifier = Modifier.weight(1f)
                        ){
                            ButtonSubmit(
                                labelValue = "in complete",
                                color = Color.Green,
                                enabled = true
                            ) {
                                invoiceViewModel.getAllMyInvoicesAsProviderAndStatus( PaymentStatus.INCOMPLETE)
                            }
                        }
                        Row (
                            modifier = Modifier.weight(1f)
                        ){
                            ButtonSubmit(
                                labelValue = "not paid",
                                color = Color.Green,
                                enabled = true
                            ) {
                                invoiceViewModel.getAllMyInvoicesAsProviderAndStatus( PaymentStatus.NOT_PAID)
                            }
                        }
                        }
                    }
                    }
                    items(invoicesAsProvider) {

                        InvoiceCard(it, appViewModel, invoiceViewModel,asProvider)

                    }
                }

            }
        }

    }
}

