package com.aymen.metastore.ui.screen.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.R
import com.aymen.metastore.model.entity.model.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.model.ReglementForProviderModel
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.PointsPaymentViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.InputTextField
import com.aymen.metastore.util.REGLEMENT_FOR_PROVIDER

@Composable
fun ReglementScreen(paymentForProviderPerDay: PaymentForProviderPerDay?, pointsPaymentViewModel: PointsPaymentViewModel, appViewModel : AppViewModel) {
    LaunchedEffect(key1 = Unit) {
        pointsPaymentViewModel.getPaymentForProviderDetails(paymentForProviderPerDay?.id!!)
    }
    val reglementForProviderPerDay = pointsPaymentViewModel.paymentForProviderPerDay.collectAsLazyPagingItems()
    val reglement = reglementForProviderPerDay.itemSnapshotList.items
    var amount by remember {
        mutableDoubleStateOf(0.0)
    }
    var amountFieald by remember {
        mutableStateOf("")
    }
    Column {
        Text(text = stringResource(id = R.string.name ,paymentForProviderPerDay?.receiver?.name?:"" ))
        Text(text = stringResource(id = R.string.rest_is,paymentForProviderPerDay?.rest.toString()))
        if(paymentForProviderPerDay?.rest != 0.0) {
            InputTextField(
                labelValue = amountFieald,
                label = stringResource(id = R.string.selling_price),
                singleLine = true,
                maxLine = 1,
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                onValueChange = {
                    if (it.matches(Regex("^[0-9]*[,.]?[0-9]*$"))) {
                        val normalizedInput = it.replace(',', '.')
                        amountFieald = normalizedInput
                        amount = if (normalizedInput.startsWith(".")) 0.0
                        else if (normalizedInput.endsWith(".")) {
                            normalizedInput.let { inp ->
                                if (inp.toDouble() % 1.0 == 0.0) inp.toDouble() else 0.0
                            }
                        } else {
                            normalizedInput.toDoubleOrNull() ?: 0.0
                        }
                    }
                }, onImage = {}, true
            ) {
            }
            ButtonSubmit(labelValue = stringResource(id = R.string.ok), color = Color.Green, enabled = true) {
                appViewModel.updateShow(REGLEMENT_FOR_PROVIDER)
                val reglemet = ReglementForProviderModel(
                    amount = amount,
                    paymentForProviderPerDay = paymentForProviderPerDay
                )
                pointsPaymentViewModel.sendReglement(reglemet)
            }
        }
        reglement.forEach{reglmnt ->
                    Text(text = stringResource(id = R.string.amount,reglmnt.amount.toString()) )
                    Text(text = stringResource(id = R.string.date,reglmnt.lastModifiedDate?:"") )
            }
        }
    }



















