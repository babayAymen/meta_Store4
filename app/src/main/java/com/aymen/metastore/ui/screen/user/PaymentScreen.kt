package com.aymen.metastore.ui.screen.user

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.model.PointsPayment
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.PaymentViewModel
import com.aymen.metastore.model.repository.ViewModel.PointsPaymentViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.BuyHistoryCard
import com.aymen.metastore.ui.component.DateFilterUI
import com.aymen.metastore.ui.screen.admin.SwipeToDeleteContainer


@Composable
fun PaymentScreen() {
    val appViewModel: AppViewModel = hiltViewModel()
    val paymentViewModel: PaymentViewModel = hiltViewModel()
    val pointPaymentViewModel: PointsPaymentViewModel = hiltViewModel()
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val context = LocalContext.current
    val type = sharedViewModel.accountType
    val company by sharedViewModel.company.collectAsStateWithLifecycle()
    val show by appViewModel.show
    var rechargeInabled by remember {
        mutableStateOf(false)
    }
    var profitInabled by remember {
        mutableStateOf(true)
    }
    var buyInabled by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(key1 = show, key2 = type) {
        if (type == AccountType.USER) {
            rechargeInabled = true
        }
        when (show) {
            "payment" -> {
                rechargeInabled = false
                profitInabled = true
                buyInabled = true
            }

            "buyhistory" -> {
                buyInabled = false
                rechargeInabled = true
                profitInabled = true
            }

            "profit" -> {
                profitInabled = false
                rechargeInabled = true
                buyInabled = true
            }
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        Column{
            if (type == AccountType.COMPANY) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row {
                        Row( // recharge history
                            modifier = Modifier.weight(1f)
                        ) {
                            ButtonSubmit(
                                labelValue = "recharge history",
                                color = Color.Green,
                                enabled = rechargeInabled
                            ) {
                                appViewModel.updateShow("payment")
                            }
                        }
                        Row( // buy history
                            modifier = Modifier.weight(1f)
                        ) {
                            ButtonSubmit(
                                labelValue = "buy history",
                                color = Color.Green,
                                enabled = buyInabled
                            ) {
                                appViewModel.updateShow("buyhistory")
                                appViewModel.updateView("allHistory")
                              //  paymentViewModel.getAllMyPaymentFromInvoice(PaymentStatus.ALL)
                            }
                        }

                        if (company.metaSeller == true) {
                            Row( // get profits
                                modifier = Modifier.weight(1f)
                            ) {
                                ButtonSubmit(
                                    labelValue = "get profits",
                                    color = Color.Green,
                                    enabled = profitInabled
                                ) {
                                    appViewModel.updateShow("profit")
                                    appViewModel.updateView("all_histories_payment_for_provider")
                                 //   paymentViewModel.getAllMyPaymentsEspece(0)
                                }
                            }
                        }
                    }
                }
            }
            when (show) {
                "payment" -> PaymentView(appViewModel, pointPaymentViewModel)
                "buyhistory" -> BuyView(appViewModel, paymentViewModel)
                "profit" -> if (type == AccountType.COMPANY && company.metaSeller == true) {
                        ProfitView(pointPaymentViewModel, paymentViewModel, appViewModel)
                    }
            }
        }
    }
}

@Composable
fun PaymentView( appViewModel: AppViewModel, pointPaymentViewModel : PointsPaymentViewModel) {

    val allPaymentRecharge = pointPaymentViewModel.allMyPointsPaymentRecharge.collectAsLazyPagingItems()
    LazyColumn {
        items(count = allPaymentRecharge.itemCount,
            key = allPaymentRecharge.itemKey { it.id!! }
        ) { index ->
            val pointPayment = allPaymentRecharge[index]
            if (pointPayment != null) {
                SwipeToDeleteContainer(
                    pointPayment,
                    onDelete = {
                        Log.e("aymenbabatdelete", "delete")
                    },
                    appViewModel = appViewModel
                ) { pointPay ->
                    Text(text = pointPay.provider?.name + " has sent " + pointPay.amount + " points for " + if (pointPay.clientUser?.username != null) pointPay.clientUser?.username else pointPay.clientCompany?.name)
                }
            }
        }
    }
}

@Composable
fun BuyView( appViewModel: AppViewModel, paymentViewModel : PaymentViewModel) {
    val myAllInvoices = paymentViewModel.myAllBuyHistory.collectAsLazyPagingItems()
    val view by appViewModel.view
    var paidInabled by remember {
        mutableStateOf(false)
    }
    var inCompleteInabled by remember {
        mutableStateOf(false)
    }
    var notPaidInabled by remember {
        mutableStateOf(false)
    }
    var notAcceptedInabled by remember {
        mutableStateOf(false)
    }
    var allHistoryInabled by remember {
        mutableStateOf(false)
    }
    when (view) {
        "payed" -> {
            allHistoryInabled = true
            paidInabled = false
            inCompleteInabled = true
            notPaidInabled = true
            notAcceptedInabled = true
        }

        "incomplete" -> {
            allHistoryInabled = true
            paidInabled = true
            inCompleteInabled = false
            notPaidInabled = true
            notAcceptedInabled = true
        }

        "notpayed" -> {
            allHistoryInabled = true
            paidInabled = true
            inCompleteInabled = true
            notPaidInabled = false
            notAcceptedInabled = true
        }

        "notaccepted" -> {
            allHistoryInabled = true
            paidInabled = true
            inCompleteInabled = true
            notPaidInabled = true
            notAcceptedInabled = false
        }

        "allHistory" -> {
            allHistoryInabled = false
            paidInabled = true
            inCompleteInabled = true
            notPaidInabled = true
            notAcceptedInabled = true
        }
    }
    Column {
        Row {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ButtonSubmit(
                    labelValue = "all",
                    color = Color.Green,
                    enabled = allHistoryInabled
                ) {
                    appViewModel.updateView("allHistory")
                    paymentViewModel.getAllMyPaymentFromInvoicee(PaymentStatus.ALL)
                }
            }
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ButtonSubmit(
                    labelValue = "payed",
                    color = Color.Green,
                    enabled = paidInabled
                ) {
                    appViewModel.updateView("payed")
                    paymentViewModel.getAllMyPaymentFromInvoicee(PaymentStatus.PAID)
                }
            }
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ButtonSubmit(
                    labelValue = "in compelete",
                    color = Color.Green,
                    enabled = inCompleteInabled
                ) {
                    appViewModel.updateView("incomplete")
                    paymentViewModel.getAllMyPaymentFromInvoicee(PaymentStatus.INCOMPLETE)
                }
            }
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ButtonSubmit(
                    labelValue = "not payed",
                    color = Color.Green,
                    enabled = notPaidInabled
                ) {
                    appViewModel.updateView("notpayed")
                    paymentViewModel.getAllMyPaymentFromInvoicee(PaymentStatus.NOT_PAID)
                }
            }
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ButtonSubmit(
                    labelValue = "not accepted yet",
                    color = Color.Green,
                    enabled = notAcceptedInabled
                ) {
                    appViewModel.updateView("notaccepted")
                    paymentViewModel.getAllMyPaymentNotAccepted()
                }
            }
        }
        when (view) {
            "allHistory" -> {
                val myAllInvoice = paymentViewModel.myAllBuyHistory.collectAsLazyPagingItems()
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(count = myAllInvoice.itemCount,
                        key = myAllInvoice.itemKey { it.id!! }
                    ) { index ->
                        val invoice = myAllInvoice[index]
                        if (invoice != null) {
                            Row {
                                Text(text = invoice.code.toString())
                                Spacer(modifier = Modifier.padding(6.dp))
                                Text(text = invoice.prix_invoice_tot.toString())
                                Spacer(modifier = Modifier.padding(6.dp))
                                Text(text = invoice.paid.toString())
                                Spacer(modifier = Modifier.padding(6.dp))
                                Text(text = if (invoice.paid == PaymentStatus.INCOMPLETE) invoice.rest.toString() else "")
                            }
                        }
                    }
                }
            }

            "payed" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(count = myAllInvoices.itemCount,
                        key = myAllInvoices.itemKey { it.id!! }
                    ) { index ->
                        val invoice = myAllInvoices[index]
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

            "incomplete" -> {
                val inComplete = paymentViewModel.inComplete.collectAsLazyPagingItems()
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

            "notpayed" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(count = myAllInvoices.itemCount,
                        key = myAllInvoices.itemKey { it.id!! }
                    ) { index ->
                        val invoice = myAllInvoices[index]
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

            "notaccepted" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(count = myAllInvoices.itemCount,
                        key = myAllInvoices.itemKey { it.id!! }
                    ) { index ->
                        val invoice = myAllInvoices[index]
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
}

@Composable
fun ProfitView( pointPaymentViewModel : PointsPaymentViewModel, paymentViewModel: PaymentViewModel, appViewModel: AppViewModel) {

    val paymentsEspece = pointPaymentViewModel.allMyPointsPaymentForProviders.collectAsLazyPagingItems()
    val myProfit by pointPaymentViewModel.myProfits.collectAsStateWithLifecycle()
    var beginDate by remember {
        mutableStateOf("")
    }
    var finalDate by remember {
        mutableStateOf("")
    }
    val view by appViewModel.view
    var allHistoryInabled by remember {
        mutableStateOf(false)
    }
    var allHistoryByDateInabled by remember {
        mutableStateOf(false)
    }
    var allProfitInabled by remember {
        mutableStateOf(false)
    }
    var allProfitByDateInabled by remember {
        mutableStateOf(false)
    }
    var sumProfitInabled by remember {
        mutableStateOf(false)
    }
    when (view) {
        "all_histories_payment_for_provider" -> {
            allHistoryInabled = false
            allHistoryByDateInabled = true
            allProfitInabled = true
            allProfitByDateInabled = true
            sumProfitInabled = true
        }

        "all_histories_payment_for_provider_by_date" -> {
            allHistoryInabled = true
            allHistoryByDateInabled = false
            allProfitInabled = true
            allProfitByDateInabled = true
            sumProfitInabled = true
        }

        "all_profit_payment_for_provider_per_day" -> {
            allHistoryInabled = true
            allHistoryByDateInabled = true
            allProfitInabled = false
            allProfitByDateInabled = true
            sumProfitInabled = true
        }
        "profit_by_date" -> {
            allHistoryInabled = true
            allHistoryByDateInabled = true
            allProfitInabled = true
            allProfitByDateInabled = false
            sumProfitInabled = true
        }
        "sum_of_profit_by_date" -> {
            allHistoryInabled = true
            allHistoryByDateInabled = true
            allProfitInabled = true
            allProfitByDateInabled = true
            sumProfitInabled = false
        }
    }
    Column {
        Row {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ButtonSubmit(
                    labelValue = "all histories",
                    color = Color.Green,
                    enabled = allHistoryInabled
                ) {
                    appViewModel.updateView("all_histories_payment_for_provider")
                }
            }
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ButtonSubmit(
                    labelValue = "history by date",
                    color = Color.Green,
                    enabled = allHistoryByDateInabled
                ) {
                    paymentViewModel.getAllMyPaymentsEspeceFromMetaByDate(
                        beginDate,
                        finalDate
                    )
                    appViewModel.updateView("all_histories_payment_for_provider_by_date")
                }
            }
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ButtonSubmit(
                    labelValue = "profit per day",
                    color = Color.Green,
                    enabled = allProfitInabled
                ) {
                    pointPaymentViewModel.getAllMyProfitsPerDay()
                    appViewModel.updateView("all_profit_payment_for_provider_per_day")
                }
            }
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ButtonSubmit(
                    labelValue = "profit by date",
                    color = Color.Green,
                    enabled = allProfitByDateInabled
                ) {
                    appViewModel.updateView("profit_by_date")
                    pointPaymentViewModel.getMyHistoryProfitByDate(
                        beginDate,
                        finalDate
                    )
                }
            }
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ButtonSubmit(
                    labelValue = "sum of profit by date",
                    color = Color.Green,
                    enabled = sumProfitInabled
                ) {
                    appViewModel.updateView("sum_of_profit_by_date")
                    pointPaymentViewModel.getMyProfitByDate(
                        beginDate,
                        finalDate
                    )
                }
            }
        }
        Row {
            DateFilterUI(paymentViewModel = paymentViewModel) { begindate, finaldate ->
                beginDate = begindate
                finalDate = finaldate
            }
        }
        Column (
            modifier = Modifier.fillMaxWidth()
        ){
            when (view) {
                "all_histories_payment_for_provider" -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(count = paymentsEspece.itemCount,
                            key = paymentsEspece.itemKey { it.id!! }
                        ) { index: Int ->
                            val payment = paymentsEspece[index]
                            if (payment != null) {
                                SwipeToDeleteContainer(
                                    payment,
                                    onDelete = {
                                        Log.e("aymenbabatdelete", "delete")
                                    },
                                    appViewModel = appViewModel
                                ) { buy ->
                                    BuyHistoryCard(buy)
                                }
                            }
                        }
                    }
                }
                "all_histories_payment_for_provider_by_date" ->{
                    val allMyProfitsPerDay = paymentViewModel.paymentsEspeceByDate.collectAsLazyPagingItems()
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            count = allMyProfitsPerDay.itemCount,
                            key = allMyProfitsPerDay.itemKey { it.id!! }
                        ) { index: Int ->
                            val payment = allMyProfitsPerDay[index]
                            if (payment != null) {
                                SwipeToDeleteContainer(
                                    payment,
                                    onDelete = {
                                        Log.e("aymenbabatdelete", "delete")
                                    },
                                    appViewModel = appViewModel
                                ) { buy ->
                                    BuyHistoryCard(buy)
                                }

                            }
                        }
                    }
                        }
                "all_profit_payment_for_provider_per_day" ->{
                            val allMyProfitsPerDay = pointPaymentViewModel.allMyProfitsPerDay.collectAsLazyPagingItems()
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    count = allMyProfitsPerDay.itemCount,
                                    key = allMyProfitsPerDay.itemKey{it.id!!}
                                ) {index :Int ->
                                    val profitPerDay = allMyProfitsPerDay[index]
                                    if(profitPerDay != null) {
                                        Row(
                                            modifier = Modifier.background(if(profitPerDay.payed== true)Color.Green else Color.Red)
                                        ) {
                                            profitPerDay.lastModifiedDate?.let { Text(text = it) }
                                            Spacer(modifier = Modifier.width(20.dp))
                                            Text(text = "${profitPerDay.amount} dt")
                                        }
                                    }
                                }
                            }
                    }
                "profit_by_date" -> {
                    val profitPerDayByDate = pointPaymentViewModel.allMyProfitsPerDayByDate.collectAsLazyPagingItems()
                    LazyColumn {
                        items(count = profitPerDayByDate.itemCount,
                            key = profitPerDayByDate.itemKey{it.id!!}
                        ){index : Int ->
                            val profit = profitPerDayByDate[index]
                            if(profit != null){
                                Row(
                                    modifier = Modifier.background(if(profit.payed== true)Color.Green else Color.Red)
                                ) {
                                    profit.lastModifiedDate?.let { Text(text = it) }
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Text(text = "${profit.amount} dt")
                                }
                            }

                        }
                    }
                }
                "sum_of_profit_by_date" ->{
                        Text(text = myProfit)
                    }

            }
        }
    }
}