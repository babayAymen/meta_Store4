package com.aymen.store.ui.screen.user

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.InvoiceViewModel
import com.aymen.store.model.repository.ViewModel.ShoppingViewModel
import com.aymen.store.ui.component.ButtonSubmit
import com.aymen.store.ui.component.DividerComponent
import com.aymen.store.ui.component.InvoiceCard
import com.aymen.store.ui.component.LodingShape
import com.aymen.store.ui.component.OrderShow
import com.aymen.store.ui.component.ShowFeesDialog
import com.aymen.store.ui.screen.admin.AddInvoiceScreen
import com.aymen.store.ui.screen.admin.OrderScreen
import java.time.LocalDateTime


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShoppingScreen() {
    val shoppingViewModel : ShoppingViewModel = hiltViewModel()
    val appViewModel : AppViewModel = hiltViewModel()
    val sharedViewModel : SharedViewModel = hiltViewModel()
    val invoiceViewModel : InvoiceViewModel = hiltViewModel()
    val myCompany by sharedViewModel.company.collectAsStateWithLifecycle()
    val myInvoicesAccepted by invoiceViewModel.myInvoicesAsClient.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = Unit) {
        shoppingViewModel.getAllMyOrders()
        if(sharedViewModel.accountType == AccountType.USER){
        invoiceViewModel.getAllMyInvoiceAsClientAndStatus(Status.INWAITING)
            invoiceViewModel.getAllMyInvoicesAsClient()
        }
    }
    val invoicesNotAccepted by invoiceViewModel.allMyInvoiceNotAccepted.collectAsStateWithLifecycle()
    DisposableEffect(Unit) {
        onDispose {
           shoppingViewModel.deleteAll()
            invoiceViewModel.deleteAll()
            Log.e("deleteall","delete all")
        }
    }
    val allMyOrders by shoppingViewModel.allMyOrders.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val show by appViewModel.show

    var showFeesDialog by remember { mutableStateOf(false) }
    var balancee by remember { mutableStateOf(0.0) }
    when(show) {
        "add invoice" -> AddInvoiceScreen(InvoiceMode.VERIFY)
        "orderLine" -> OrderScreen()
        "orderLineDetails" -> PurchaseOrderDetailsScreen(shoppingViewModel = shoppingViewModel)
        else -> {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(// all my orders those not send yet
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {

                            if (shoppingViewModel.orderArray.isNotEmpty()) {

                                LazyColumn(

                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    itemsIndexed(shoppingViewModel.orderArray) { index, it ->
                                        Row {
                                            Row(
                                                modifier = Modifier.weight(0.8f)
                                            ) {

                                                OrderShow(order = it)
                                            }
                                            Row(
                                                modifier = Modifier.weight(0.1f)
                                            ) {
                                                IconButton(onClick = {
                                                    shoppingViewModel.beforSendOrder {isAccepted , balance ->
                                                        if(isAccepted){
                                                             shoppingViewModel.sendOrder(index, balance)
                                                        }else{
                                                            showFeesDialog = true
                                                            balancee = balance
                                                        }
                                                    }
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "send",
                                                        tint = Color.Magenta
                                                    )
                                                }
                                            }
                                            if(showFeesDialog){
                                                ShowFeesDialog(isOpen = true) {submitfees ->
                                                    if(submitfees){
                                                        shoppingViewModel.sendOrder(-1,balancee)
                                                    }else{
                                                        showFeesDialog = false
                                                    }
                                                }
                                            }
                                            Row(
                                                modifier = Modifier.weight(0.1f)
                                            ) {
                                                IconButton(onClick = {
                                                    shoppingViewModel.removeOrderById(index)
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Default.Remove,
                                                        contentDescription = "remove",
                                                        tint = Color.Red
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                Row {
                                    Row(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        ButtonSubmit(
                                            labelValue = "Cancel All",
                                            color = Color.Red,
                                            enabled = true
                                        ) {
                                            shoppingViewModel.returnAllMyMony()
                                        }
                                    }
                                    Row(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        ButtonSubmit(
                                            labelValue = "Send All",
                                            color = Color.Green,
                                            enabled = true
                                        ) {
                                            shoppingViewModel.beforSendOrder {isAccepte , balance ->
                                                if(isAccepte){
                                                     shoppingViewModel.sendOrder(-1,balance)
                                                }else{
                                                    showFeesDialog = true
                                                }
                                            }
                                        }
                                    }
                                }
                                        DividerComponent()
                            }
                        }
                    }
                    Row( // all my invoices those do not accepted
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LazyColumn {
                            items(invoicesNotAccepted) { invoice ->
                                InvoiceCard(
                                    invoice = invoice,
                                    appViewModel = appViewModel,
                                    invoiceViewModel = invoiceViewModel,
                                    asProvider = false
                                )
                            }
                            item {
                                DividerComponent()
                            }
                        }
                    }
                    Row( // all my orders those do not accepted
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            if (shoppingViewModel.isLoading) {
                                item {
                                    LodingShape()
                                }
                            }
                            items(allMyOrders) {
                                val dateTime = LocalDateTime.parse(it.purchaseOrder.createdDate)
                                val date = dateTime.toLocalDate()
                                Text(text =
                                if (it.company.id == myCompany.id) "you have an order from ${it.person?.username ?: it.client?.name}" else "you have sent an order to ${it.company.name}",
                                    modifier = Modifier.clickable {
                                        shoppingViewModel.Order = it.purchaseOrder
                                        appViewModel.updateShow("orderLineDetails")
                                    }
                                )
                                Text(
                                    text = date.toString(),
                                    style = TextStyle(fontSize = 8.sp)
                                )
                            }
                            item {
                                DividerComponent()
                            }

                        }
                    }
                    Row (// all my invoices those are accepted and orders also accepted
                        modifier = Modifier.fillMaxWidth()
                    ){
                        LazyColumn {
                            items(myInvoicesAccepted){
                                Log.e("myinvoicesaccepted","size is !: ${myInvoicesAccepted.size}")
                                InvoiceCard(
                                    invoice = it,
                                    appViewModel = appViewModel,
                                    invoiceViewModel = invoiceViewModel,
                                    asProvider = false)
                            }
                        }
                    }
                }
            }
        }
    }
}