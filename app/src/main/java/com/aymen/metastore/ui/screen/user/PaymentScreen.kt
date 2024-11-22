package com.aymen.metastore.ui.screen.user

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
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
import com.aymen.metastore.ui.component.LodingShape
import com.aymen.metastore.ui.screen.admin.SwipeToDeleteContainer


@Composable
fun PaymentScreen() {
    val appViewModel : AppViewModel = hiltViewModel()
    val paymentViewModel : PaymentViewModel = hiltViewModel()
    val pointPaymentViewModel : PointsPaymentViewModel = hiltViewModel()
    val sharedViewModel : SharedViewModel = hiltViewModel()
    val isLoading = sharedViewModel.isLoading
    val context = LocalContext.current
    val type = sharedViewModel.accountType
    val company by sharedViewModel.company.collectAsStateWithLifecycle()
    val allMyPointsPayment = pointPaymentViewModel.allMyPointsPayment.collectAsLazyPagingItems()

    val show by appViewModel.show
    val myAllInvoices = paymentViewModel.myAllInvoice.collectAsLazyPagingItems()
    

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
        if(type == AccountType.USER){
                rechargeInabled = true
        }
        when(show){
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
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (type == AccountType.COMPANY) {
                item {
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
                                    paymentViewModel.getAllMyPaymentFromInvoice(PaymentStatus.PAID)
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
                                        paymentViewModel.getAllMyPaymentsEspece(0)

                                    }
                                }
                            }
                        }
                    }
                }
            }
            when(show){
                "payment" ->
                    item {
                        if (isLoading) {
                            LodingShape()
                        } else {
                            PaymentView( allMyPointsPayment, appViewModel)
                        }
                    }
                "buyhistory"->{
                    item {
                            Row {
                                Row(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    ButtonSubmit(
                                        labelValue = "payed",
                                        color = Color.Green,
                                        enabled = true
                                    ) {
                                        appViewModel.updateView("payed")
                                        paymentViewModel.getAllMyPaymentFromInvoice(PaymentStatus.PAID)
                                    }
                                }
                                Row(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    ButtonSubmit(
                                        labelValue = "in compelete",
                                        color = Color.Green,
                                        enabled = true
                                    ) {
                                        appViewModel.updateView("incomplete")
                                        paymentViewModel.getAllMyPaymentFromInvoice(PaymentStatus.INCOMPLETE)
                                    }
                                }
                                Row(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    ButtonSubmit(
                                        labelValue = "not payed",
                                        color = Color.Green,
                                        enabled = true
                                    ) {
                                        appViewModel.updateView("notpayed")
                                        paymentViewModel.getAllMyPaymentFromInvoice(PaymentStatus.NOT_PAID)
                                    }
                                }
                                Row(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    ButtonSubmit(
                                        labelValue = "not accepted yet",
                                        color = Color.Green,
                                        enabled = true
                                    ) {
                                        appViewModel.updateView("notaccepted")
                                        paymentViewModel.getAllMyPaymentNotAccepted()
                                    }
                                }
                            }
                        }

                    if (isLoading) {
                        item {
                        LodingShape()
                        }
                    } else {
                        items(myAllInvoices.itemCount) {index ->
                            val invoice = myAllInvoices[index]
                            BuyView(invoice!!, appViewModel, isLoading)
                        }
                    }
                }

                    "profit" ->
                     if(type == AccountType.COMPANY && company.metaSeller == true){
                    item {
                        ProfitView(isLoading, pointPaymentViewModel, paymentViewModel, appViewModel)
                    }

                }
            }



        }
    }
}

@Composable
fun PaymentView(allMyPointsPayment : LazyPagingItems<PointsPayment>, appViewModel: AppViewModel) {
            for(index in 0 until allMyPointsPayment.itemCount) {
                val pointPayment = allMyPointsPayment[index]
                SwipeToDeleteContainer(
                    pointPayment,
                    onDelete = {
                        Log.e("aymenbabatdelete", "delete")
                    },
                    appViewModel = appViewModel
                ) { pointPay ->
                    Text(text = pointPay?.provider?.name + " has sent " + pointPay?.amount + " points for " + if (pointPay?.clientUser?.username != null) pointPay.clientUser?.username else pointPay?.clientCompany?.name)
                }

            }

}

@Composable
fun BuyView(invoice : Invoice, appViewModel: AppViewModel, isLoading : Boolean) {
    val view by appViewModel.view
    if (isLoading){
        LodingShape()
    }else {
        when (view) {

            "payed" -> {
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

            "incomplete" -> {
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

            "notpayed" -> {
                Row {
                    Text(text = invoice.code.toString())
                    Spacer(modifier = Modifier.padding(6.dp))
                    Text(
                        text = "client name :" + (invoice.client?.name ?: invoice.person?.username)
                    )
                    Spacer(modifier = Modifier.padding(6.dp))
                    Text(text = "invoice date :" + invoice.lastModifiedDate)
                }
            }

            "notaccepted" -> {
                Row {
                    Text(text = invoice.code.toString())
                    Spacer(modifier = Modifier.padding(6.dp))
                    Text(
                        text = "client name :" + (invoice.client?.name ?: invoice.person?.username)
                    )
                    Spacer(modifier = Modifier.padding(6.dp))
                    Text(text = "invoice date :" + invoice.lastModifiedDate)

                }
            }
        }
    }
}

@Composable
fun ProfitView(isLoading: Boolean, pointPaymentViewModel : PointsPaymentViewModel, paymentViewModel: PaymentViewModel, appViewModel: AppViewModel) {

    val allMyProfits = pointPaymentViewModel.allMyProfits.collectAsLazyPagingItems()
    val paymentsEspece = paymentViewModel.paymentsEspece.collectAsLazyPagingItems()
    val myProfit by pointPaymentViewModel.myProfits.collectAsStateWithLifecycle()
    var beginDate by remember {
        mutableStateOf("")
    }
    var finalDate by remember {
        mutableStateOf("")
    }
            Row {
                Column (
                    modifier = Modifier.weight(1f)
                ){

                    ButtonSubmit(
                        labelValue = "history by date",
                        color = Color.Green,
                        enabled = true
                    ) {
                        paymentViewModel.getAllMyPaymentsEspeceFromMetaByDate(
                            beginDate,
                            finalDate
                        )
                    }
                    DateFilterUI(paymentViewModel = paymentViewModel) { begindate, finaldate ->
                        beginDate = begindate
                        finalDate = finaldate
                    }

                    if(isLoading) {
                        LodingShape()
                    }else{
                        for(index in 0 until paymentsEspece.itemCount) {
                            val payment = paymentsEspece[index]
                            SwipeToDeleteContainer(
                                payment,
                                onDelete = {
                                    Log.e("aymenbabatdelete", "delete")
                                },
                                appViewModel = appViewModel
                            ) { buy ->
                                BuyHistoryCard(buy!!)
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    ButtonSubmit(
                        labelValue = "profit per day",
                        color = Color.Green,
                        enabled = true
                    ) {
                        pointPaymentViewModel.getAllMyProfits()
                    }

                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    ButtonSubmit(
                        labelValue = "profit by date",
                        color = Color.Green,
                        enabled = true
                    ) {
                        pointPaymentViewModel.getMyHistoryProfitByDate(
                            beginDate,
                            finalDate
                        )
                    }
                    DateFilterUI(paymentViewModel = paymentViewModel) { begindate, finaldate ->
                        beginDate = begindate
                        finalDate = finaldate
                    }
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    ButtonSubmit(
                        labelValue = "sum of profit by date",
                        color = Color.Green,
                        enabled = true
                    ) {
                        pointPaymentViewModel.getMyProfitByDate(
                            beginDate,
                            finalDate
                        )
                    }
                    DateFilterUI(paymentViewModel = paymentViewModel) { begindate, finaldate ->
                        beginDate = begindate
                        finalDate = finaldate
                    }
                    if(isLoading){
                        LodingShape()
                    }else {
                        Text(text = myProfit)
                    }
                }

            }

        if (isLoading) {
                LodingShape()
        } else {
            for(index in 0 until allMyProfits.itemCount) {
                val profit = allMyProfits[index]

                Column {
                    Text(text = profit?.amount.toString())
                    Text(text = profit?.payed.toString())
                }
            }
        }

}