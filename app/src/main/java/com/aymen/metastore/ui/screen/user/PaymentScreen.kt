package com.aymen.store.ui.screen.user

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.entity.realm.Invoice
import com.aymen.store.model.entity.realm.PointsPayment
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.PaymentViewModel
import com.aymen.store.model.repository.ViewModel.PointsPaymentViewModel
import com.aymen.store.ui.component.ButtonSubmit
import com.aymen.store.ui.component.BuyHistoryCard
import com.aymen.store.ui.component.DateFilterUI
import com.aymen.store.ui.component.LodingShape
import com.aymen.store.ui.component.PaymentCard
import com.aymen.store.ui.screen.admin.SwipeToDeleteContainer


@Composable
fun PaymentScreen() {
    val appViewModel : AppViewModel = hiltViewModel()
    val paymentViewModel : PaymentViewModel = hiltViewModel()
    val pointPaymentViewModel : PointsPaymentViewModel = hiltViewModel()
    val sharedViewModel : SharedViewModel = hiltViewModel()
    val isLoading = pointPaymentViewModel.isLoding
    val isLoding = paymentViewModel.isLoding
    LaunchedEffect(key1 = Unit) {
        pointPaymentViewModel.getAllMyPointsPayment()
    }
    val type = sharedViewModel.accountType
    val allMyPointsPayment by pointPaymentViewModel.allMyPointsPayment.collectAsStateWithLifecycle()

    val show by appViewModel.show
    val context = LocalContext.current
    val myAllInvoices by paymentViewModel.myAllInvoice.collectAsStateWithLifecycle()
    

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
                                    paymentViewModel.getAllMyPaymentFromInvoice(PaymentStatus.INCOMPLETE)
                                }
                            }
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
            when(show){
                "payment" ->
                    item {
                        paymentView(isLoading, allMyPointsPayment, appViewModel)
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
                                }
                            }
                        }
                    }
                    items(myAllInvoices){
                        BuyView(it, appViewModel)
                    }
                }
                "profit" ->
                    item {
                      ProfitView(isLoding, pointPaymentViewModel, paymentViewModel, appViewModel)
                    }

            }



        }
    }
}

@Composable
fun paymentView(isLoading : Boolean,allMyPointsPayment : List<PointsPayment>, appViewModel: AppViewModel ) {

        if (isLoading){
                LodingShape()
        }else {
            allMyPointsPayment.forEach {
                SwipeToDeleteContainer(
                    it,
                    onDelete = {
                        Log.e("aymenbabatdelete", "delete")
                    },
                    appViewModel = appViewModel
                ) { pointPay ->
                    Text(text = pointPay.provider?.name.toString() + " has sent " + pointPay.amount + " points for " + if (pointPay.clientUser?.username != null) pointPay.clientUser?.username else pointPay.clientCompany?.name)
                }

            }
        }
}

@Composable
fun BuyView(invoice : Invoice, appViewModel: AppViewModel) {
    val view by appViewModel.view
    when(view) {
        "notpayed" -> {
            Row {
                Text(text = "hi")
            }
        }
        "incomplete" -> {
            Row {
                Text(text = "hello")
            }
        }

        "payed" -> {
            Row {
                Text(text = invoice.code.toString())
                Spacer(modifier = Modifier.padding(6.dp))
                Text(text = invoice.prix_invoice_tot.toString())
                Spacer(modifier = Modifier.padding(6.dp))
                Text(text = invoice.paid)
                Spacer(modifier = Modifier.padding(6.dp))
                Text(text = if (invoice.paid == PaymentStatus.INCOMPLETE.toString()) invoice.rest.toString() else "")
            }
        }
    }
}

@Composable
fun ProfitView(isLoding: Boolean, pointPaymentViewModel : PointsPaymentViewModel, paymentViewModel: PaymentViewModel, appViewModel: AppViewModel) {

    val allMyProfits by pointPaymentViewModel.allMyProfits.collectAsStateWithLifecycle()
    val paymentsEspece by paymentViewModel.paymentsEspece.collectAsStateWithLifecycle()
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
                        paymentViewModel.getAllMyPaymentsEspeceByDate(
                            beginDate,
                            finalDate
                        )
                    }
                    DateFilterUI(paymentViewModel = paymentViewModel) { begindate, finaldate ->
                        beginDate = begindate
                        finalDate = finaldate
                    }

                    if(paymentViewModel.isLoding) {
                        LodingShape()
                    }else{
                        paymentsEspece.forEach {
                            SwipeToDeleteContainer(
                                it,
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
                    if(pointPaymentViewModel.isLoding){
                        LodingShape()
                    }else {
                        Text(text = myProfit)
                    }
                }

            }

        if (isLoding) {
                LodingShape()
        } else {
            allMyProfits.forEach {
                Column {
                    Text(text = it.amount.toString())
                    Text(text = it.payed.toString())
                }
            }
        }

}