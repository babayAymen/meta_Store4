package com.aymen.store.ui.screen.user

import android.util.Log
import android.widget.Toast
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
    LaunchedEffect(key1 = Unit) {
        pointPaymentViewModel.getAllMyPointsPayment()
    }
    val type = sharedViewModel.accountType
    val paymentsEspece by paymentViewModel.paymentsEspece.collectAsStateWithLifecycle()
    val allMyPointsPayment by pointPaymentViewModel.allMyPointsPayment.collectAsStateWithLifecycle()
    val allMyProfits by pointPaymentViewModel.allMyProfits.collectAsStateWithLifecycle()

    val show by appViewModel.show
    val context = LocalContext.current
    val myProfit by pointPaymentViewModel.myProfits.collectAsStateWithLifecycle()
    var beginDate by remember {
        mutableStateOf("")
    }
    var finalDate by remember {
        mutableStateOf("")
    }
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
            "pointespece" -> {
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
                            Column {
                                Row {

                                    Row(
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
                                    Row(
                                        modifier = Modifier.weight(1f)
                                    ) {

                                        ButtonSubmit(
                                            labelValue = "buy history",
                                            color = Color.Green,
                                            enabled = buyInabled
                                        ) {
                                            appViewModel.updateShow("pointespece")
                                            paymentViewModel.getAllMyPaymentsEspece(0)

                                        }
                                    }
                                        Row(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            ButtonSubmit(
                                                labelValue = "get profits",
                                                color = Color.Green,
                                                enabled = profitInabled
                                            ) {
                                                appViewModel.updateShow("profit")
                                                pointPaymentViewModel.getAllMyProfits()

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

            when(show){
                "pointespece"->{
                    item {

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
                    }
                    if(paymentViewModel.isLoding) {
                        item {
                            LodingShape()
                        }
                    }else{
                        items(paymentsEspece) {
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
                "payment" ->{
                    if (pointPaymentViewModel.isLoding){
                        item {
                            LodingShape()
                        }
                    }else {
                        items(allMyPointsPayment) {
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
                "profit" -> {
                    item {
                        Row {
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
                    }
                    if (paymentViewModel.isLoding) {
                        item {
                           LodingShape()
                        }
                    } else {
                        items(allMyProfits) {
                            Column {
                                Text(text = it.amount.toString())
                                Text(text = it.payed.toString())
                            }
                        }
                    }
                }

            }



        }
    }
}
