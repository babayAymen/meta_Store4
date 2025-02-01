package com.aymen.metastore.ui.screen.user

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.R
import com.aymen.metastore.dependencyInjection.NetworkUtil
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.InvoiceViewModel
import com.aymen.metastore.model.repository.ViewModel.PaymentViewModel
import com.aymen.metastore.model.repository.ViewModel.PointsPaymentViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.BuyHistoryCard
import com.aymen.metastore.ui.component.DateFilterUI
import com.aymen.metastore.ui.component.InvoiceCard
import com.aymen.metastore.ui.screen.admin.AddInvoiceScreen
import com.aymen.metastore.ui.screen.admin.ReglementCompany
import com.aymen.metastore.ui.screen.admin.SwipeToDeleteContainer
import com.aymen.metastore.util.ADD_INVOICE
import com.aymen.metastore.util.ALL
import com.aymen.metastore.util.ALL_HISTORY
import com.aymen.metastore.util.BUY_HISTORY
import com.aymen.metastore.util.PAID
import com.aymen.metastore.util.PAYMENT
import com.aymen.metastore.util.PROFIT
import com.aymen.metastore.util.REGLEMENT
import com.aymen.metastore.util.all_histories_payment_for_provider
import com.aymen.metastore.util.all_histories_payment_for_provider_by_date
import com.aymen.metastore.util.all_profit_payment_for_provider_per_day
import com.aymen.metastore.util.incomplete
import com.aymen.metastore.util.notaccepted
import com.aymen.metastore.util.notpayed
import com.aymen.metastore.util.payed
import com.aymen.metastore.util.profit_by_date
import com.aymen.metastore.util.sum_of_profit_by_date
import com.aymen.store.model.Enum.RoleEnum


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PaymentScreen() {
    val appViewModel: AppViewModel = hiltViewModel()
    val invoiceViewModel: InvoiceViewModel = hiltViewModel()
    val paymentViewModel: PaymentViewModel = hiltViewModel()
    val pointPaymentViewModel: PointsPaymentViewModel = hiltViewModel()
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val type by sharedViewModel.accountType.collectAsStateWithLifecycle()
    val company by sharedViewModel.company.collectAsStateWithLifecycle()
    val user by sharedViewModel.user.collectAsStateWithLifecycle()
    sharedViewModel.setPaymentCountNotification(true)
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
    var reglementInabled by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(key1 = show) {
        when (show) {
            PAYMENT -> {
                rechargeInabled = false
                profitInabled = true
                buyInabled = true
                reglementInabled = true
            }

            BUY_HISTORY -> {
                buyInabled = false
                rechargeInabled = true
                profitInabled = true
                reglementInabled = true
            }

            PROFIT -> {
                profitInabled = false
                rechargeInabled = true
                buyInabled = true
                reglementInabled = true
            }
            REGLEMENT ->{
                reglementInabled = false
                profitInabled = true
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
            if (type == AccountType.COMPANY && user.role != RoleEnum.WORKER) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row {
                            Row( // recharge history
                                modifier = Modifier.weight(1f)
                            ) {
                                ButtonSubmit(
                                    labelValue = stringResource(id = R.string.recharge_history),
                                    color = Color.Green,
                                    enabled = rechargeInabled
                                ) {
                                    appViewModel.updateShow(PAYMENT)
                                }
                            }
                        Row( // buy history
                            modifier = Modifier.weight(1f)
                        ) {
                            ButtonSubmit(
                                labelValue = stringResource(id = R.string.buy_history),
                                color = Color.Green,
                                enabled = buyInabled
                            ) {
                                invoiceViewModel.setFilter(PaymentStatus.ALL)
                                appViewModel.updateShow(BUY_HISTORY)
                                appViewModel.updateView(ALL_HISTORY)
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
                                    appViewModel.updateShow(PROFIT)
                                    appViewModel.updateView(all_histories_payment_for_provider)
                                }
                            }
                        }
//                        Row (modifier = Modifier.weight(1f)){
//                            ButtonSubmit(labelValue = "reglement", color = Color.Green, enabled =reglementInabled) {
//                                appViewModel.updateView("reglement")
//                            }
//                        }
                    }
                }
            }
            when (show) {
                PAYMENT -> PaymentView(pointPaymentViewModel,company)
                BUY_HISTORY -> {
                    BuyView(appViewModel, invoiceViewModel)
                }
                PROFIT -> if (type == AccountType.COMPANY && company.metaSeller == true) {
                        ProfitView(pointPaymentViewModel, paymentViewModel, appViewModel)
                    }
                REGLEMENT -> ReglementCompany()
            }
        }
    }
}

@Composable
fun PaymentView( pointPaymentViewModel : PointsPaymentViewModel , myCompany : Company ) {
    val allPaymentRecharge = pointPaymentViewModel.allMyPointsPaymentRecharge.collectAsLazyPagingItems()
    LazyColumn {
        items(count = allPaymentRecharge.itemCount,
            key = allPaymentRecharge.itemKey { it.id!! }
        ) { index ->
            val pointPayment = allPaymentRecharge[index]
            if (pointPayment != null) {
                val paymentDetails = if (pointPayment.provider?.id == myCompany.id) stringResource(
                    id = R.string.your_sent_point,pointPayment.amount.toString(),pointPayment.clientUser?.username ?: pointPayment.clientCompany?.name ?: ""
                )
                 else
                     stringResource(id = R.string.his_sent_point,pointPayment.provider?.name?:"",pointPayment.amount?:"")

                Text(text = paymentDetails)

            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BuyView( appViewModel: AppViewModel, invoiceViewModel: InvoiceViewModel) {
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
        ADD_INVOICE -> {
            allHistoryInabled = true
            paidInabled = true
            inCompleteInabled = true
            notPaidInabled = true
            notAcceptedInabled = true
        }
        payed -> {
            allHistoryInabled = true
            paidInabled = false
            inCompleteInabled = true
            notPaidInabled = true
            notAcceptedInabled = true
            invoiceViewModel.setFilter(PaymentStatus.PAID)
        }

        incomplete -> {
            allHistoryInabled = true
            paidInabled = true
            inCompleteInabled = false
            notPaidInabled = true
            notAcceptedInabled = true
            invoiceViewModel.setFilter(PaymentStatus.INCOMPLETE)
        }

        notpayed -> {
            allHistoryInabled = true
            paidInabled = true
            inCompleteInabled = true
            notPaidInabled = false
            notAcceptedInabled = true
            invoiceViewModel.setFilter(PaymentStatus.NOT_PAID)
        }

        notaccepted -> {
            allHistoryInabled = true
            paidInabled = true
            inCompleteInabled = true
            notPaidInabled = true
            notAcceptedInabled = false
            invoiceViewModel.getAllMyPaymentNotAccepted(true)
        }

        ALL_HISTORY -> {
            allHistoryInabled = false
            paidInabled = true
            inCompleteInabled = true
            notPaidInabled = true
            notAcceptedInabled = true
            invoiceViewModel.setFilter(PaymentStatus.ALL)
        }
    }

    val myAllInvoice = invoiceViewModel.invoices.collectAsLazyPagingItems()
    Column {
        Row {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ButtonSubmit(
                    labelValue = stringResource(id = R.string.all),
                    color = Color.Green,
                    enabled = allHistoryInabled
                ) {
                    appViewModel.updateView(ALL_HISTORY)
                }
            }
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ButtonSubmit(
                    labelValue = stringResource(id = R.string.paid),
                    color = Color.Green,
                    enabled = paidInabled
                ) {
                    appViewModel.updateView(payed)
                }
            }
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ButtonSubmit(
                    labelValue = stringResource(id = R.string.in_complete),
                    color = Color.Green,
                    enabled = inCompleteInabled
                ) {
                    appViewModel.updateView(incomplete)
                }
            }
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ButtonSubmit(
                    labelValue = stringResource(id = R.string.not_paid),
                    color = Color.Green,
                    enabled = notPaidInabled
                ) {
                    appViewModel.updateView(notpayed)
                }
            }
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ButtonSubmit(
                    labelValue = stringResource(id = R.string.not_accepted_yet),
                    color = Color.Green,
                    enabled = notAcceptedInabled
                ) {
                    appViewModel.updateView(notaccepted)
                }
            }
        }
        when (view) {
            ADD_INVOICE ->{
                AddInvoiceScreen()
            }
            ALL_HISTORY -> {
                        val listState = invoiceViewModel.listState
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(count = myAllInvoice.itemCount,
                                key = myAllInvoice.itemKey { it.id!! }
                            ) { index ->
                                val invoice = myAllInvoice[index]
                                if (invoice != null) {
                                    InvoiceCard(
                                        invoice = invoice,
                                        appViewModel = appViewModel,
                                        invoiceViewModel = invoiceViewModel,
                                        asProvider = true
                                    )
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
            payed -> {
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
                            }
                        }
                    }
                }
            }
            incomplete -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(count = myAllInvoice.itemCount,
                        key = myAllInvoice.itemKey { it.id!! }
                    ) { index ->
                        val invoice = myAllInvoice[index]
                        if (invoice != null) {
                            Row {
                                Text(text = stringResource(id = R.string.invoice_code,invoice.code.toString()) )
                                Spacer(modifier = Modifier.padding(6.dp))
                                Text(
                                    text = stringResource(id = R.string.client_name,invoice.person?.username ?: invoice.client?.name!!))
                                Spacer(modifier = Modifier.padding(6.dp))
                                Text(text = stringResource(id = R.string.rest_is,invoice.rest.toString())  )
                            }
                        }
                    }
                }
            }
            notpayed -> {
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
                                Text(
                                    text = stringResource(id = R.string.client_name,invoice.client?.name ?: invoice.person?.username.toString())
                                )
                                Spacer(modifier = Modifier.padding(6.dp))
                                Text(text = stringResource(id = R.string.invoice_date,invoice.lastModifiedDate.toString()))
                            }
                        }
                    }
                }
            }
            notaccepted -> {
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
                                        ?: invoice.person?.username.toString())
                                )
                                Spacer(modifier = Modifier.padding(6.dp))
                                Text(text = stringResource(id = R.string.invoice_date,invoice.lastModifiedDate.toString()))
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
val sharedViewModel : SharedViewModel = hiltViewModel()
    val paymentsEspece = pointPaymentViewModel.allMyPointsPaymentForProviders.collectAsLazyPagingItems()
    LaunchedEffect(key1 = paymentsEspece) {
        if(paymentsEspece.itemCount == 0) {
            pointPaymentViewModel.getAllMyPointsPaymentt(sharedViewModel.company.value.id ?: 0)
        }
    }
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
        all_histories_payment_for_provider -> {
            allHistoryInabled = false
            allHistoryByDateInabled = true
            allProfitInabled = true
            allProfitByDateInabled = true
            sumProfitInabled = true
        }
        all_histories_payment_for_provider_by_date -> {
            allHistoryInabled = true
            allHistoryByDateInabled = false
            allProfitInabled = true
            allProfitByDateInabled = true
            sumProfitInabled = true
        }
        all_profit_payment_for_provider_per_day -> {
            allHistoryInabled = true
            allHistoryByDateInabled = true
            allProfitInabled = false
            allProfitByDateInabled = true
            sumProfitInabled = true
        }
        profit_by_date -> {
            allHistoryInabled = true
            allHistoryByDateInabled = true
            allProfitInabled = true
            allProfitByDateInabled = false
            sumProfitInabled = true
        }
        sum_of_profit_by_date -> {
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
                    labelValue = stringResource(id = R.string.all_histories),
                    color = Color.Green,
                    enabled = allHistoryInabled
                ) {
                    appViewModel.updateView(all_histories_payment_for_provider)
                }
            }
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ButtonSubmit(
                    labelValue = stringResource(id = R.string.history_by_date),
                    color = Color.Green,
                    enabled = allHistoryByDateInabled
                ) {
                    paymentViewModel.getAllMyPaymentsEspeceFromMetaByDate(
                        beginDate,
                        finalDate
                    )
                    appViewModel.updateView(all_histories_payment_for_provider_by_date)
                }
            }
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ButtonSubmit(
                    labelValue = stringResource(id = R.string.profit_per_day),
                    color = Color.Green,
                    enabled = allProfitInabled
                ) {
                    appViewModel.updateView(all_profit_payment_for_provider_per_day)
                }
            }
            Row(
                modifier = Modifier.weight(1f)
            ) {
                ButtonSubmit(
                    labelValue = stringResource(id = R.string.profit_by_date),
                    color = Color.Green,
                    enabled = allProfitByDateInabled
                ) {
                    appViewModel.updateView(profit_by_date)
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
                    labelValue = stringResource(id = R.string.sum_of_profit_by_date),
                    color = Color.Green,
                    enabled = sumProfitInabled
                ) {
                    appViewModel.updateView(sum_of_profit_by_date)
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
                all_histories_payment_for_provider -> {
                    val listState = pointPaymentViewModel.listState
                            LazyColumn(state = listState,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(count = paymentsEspece.itemCount,
                                    key = { index ->
                                        val item = paymentsEspece[index]
                                        item?.id ?: index
                                    }
                                ) { index: Int ->
                                    val payment = paymentsEspece[index]
                                    if (payment != null) {
                                            BuyHistoryCard(payment)
                                    }
                                }
                            }
                }

                all_histories_payment_for_provider_by_date->{
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
                                    BuyHistoryCard(payment)

                            }
                        }
                    }
                        }
                all_profit_payment_for_provider_per_day ->{
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
                                            modifier = Modifier.background( if(profitPerDay.isPayed== true)Color.Green else if(profitPerDay.rest != 0.0 && profitPerDay.rest != profitPerDay.amount ) Color.Blue else Color.Red)
                                        ) {
                                            profitPerDay.lastModifiedDate?.let { Text(text = it) }
                                            Spacer(modifier = Modifier.width(20.dp))
                                            Text(text = stringResource(id = R.string.dt,profitPerDay.amount.toString()) )
                                        }
                                    }
                                }
                            }
                    }
                profit_by_date -> {
                    val profitPerDayByDate = pointPaymentViewModel.allMyProfitsPerDayByDate.collectAsLazyPagingItems()
                    LazyColumn {
                        items(count = profitPerDayByDate.itemCount,
                            key = profitPerDayByDate.itemKey{it.id!!}
                        ){index : Int ->
                            val profit = profitPerDayByDate[index]
                            if(profit != null){
                                Row(
                                    modifier = Modifier.background(if(profit.isPayed== true)Color.Green else Color.Red)
                                ) {
                                    profit.lastModifiedDate?.let { Text(text = it) }
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Text(text = stringResource(id = R.string.dt,profit.amount.toString()))
                                }
                            }

                        }
                    }
                }
                sum_of_profit_by_date ->{
                    val myProfit by pointPaymentViewModel.myProfits.collectAsStateWithLifecycle()
                        Text(text = myProfit)
                    }

            }
        }
    }
}