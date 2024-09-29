package com.aymen.store.ui.screen.user

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.repository.ViewModel.CompanyViewModel
import com.aymen.store.model.repository.ViewModel.ShoppingViewModel
import com.aymen.store.ui.component.ButtonSubmit
import com.aymen.store.ui.component.LodingShape
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable
fun PurchaseOrderDetailsScreen(shoppingViewModel : ShoppingViewModel) {

    val companyViewModel: CompanyViewModel = hiltViewModel()
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val context = LocalContext.current
    var hasWaitingStatus by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        shoppingViewModel.getAllMyOrdersLine()

    }
    val allMyOrdersLine by shoppingViewModel.allMyOrdersLine.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = allMyOrdersLine) {
        hasWaitingStatus = if(allMyOrdersLine.any{it.status == Status.INWAITING.toString()}){
            true
        }else{
            false
        }
    }
    DisposableEffect(key1 = Unit) {
        onDispose {
            shoppingViewModel.clearAllOrdersLine()
        }
    }
    val myCompany by sharedViewModel.company.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp)
    ) {
        if (allMyOrdersLine.isEmpty() || shoppingViewModel.isLoading) {
            // Display loading indicator or empty state
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LodingShape()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                if(shoppingViewModel.Order.company?.id == myCompany.id) {
                    item {
                        if (hasWaitingStatus) {
                            Row {
                                Row(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    ButtonSubmit(
                                        labelValue = "Accept All",
                                        color = Color.Green,
                                        enabled = true,
                                    ) {
                                        shoppingViewModel.orderLineResponse(
                                            status = Status.ACCEPTED.toString(),
                                            id = shoppingViewModel.Order.id!!,
                                            isAll = true
                                        )
                                        hasWaitingStatus = false
                                    }
                                }
                                Row(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    ButtonSubmit(
                                        labelValue = "Refuse All",
                                        color = Color.Red,
                                        enabled = true,
                                    ) {
                                        shoppingViewModel.orderLineResponse(
                                            status = Status.REFUSED.toString(),
                                            id = shoppingViewModel.Order.id!!,
                                            isAll = true
                                        )
                                        hasWaitingStatus = false
                                    }
                                }
                            }
                        }
                    }
                }else{
                    if(hasWaitingStatus){
                        item {
                            Row {
                                ButtonSubmit(
                                    labelValue = "Cancel All",
                                    color = Color.Red,
                                    enabled = true
                                ) {
                                    shoppingViewModel.orderLineResponse(
                                        status = Status.CANCELLED.toString(),
                                        id = shoppingViewModel.Order.id!!,
                                        isAll = true
                                    )
                                    hasWaitingStatus = false
                                }
                            }
                        }
                    }
                }
                items(allMyOrdersLine) { line ->
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = line.quantity.toString())
                        Text(text = line.delivery.toString())
                        Text(text = line.article?.article?.libelle ?: "")
                        Text(text = line.purchaseorder?.orderNumber.toString())
                        Text(text = line.comment)
                        Text(text = line.status)
                        when (line.status) {
                            Status.INWAITING.toString() -> {
                                Toast.makeText(context, "${line.purchaseorder?.company?.id} azzz ${myCompany.id}", Toast.LENGTH_SHORT).show()
                                if (line.purchaseorder?.company?.id == myCompany.id) {
                                    Row {
                                        Row(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                        ButtonSubmit(
                                            labelValue = "Accept",
                                            color = Color.Green,
                                            enabled = true,
                                        ) {
                                            shoppingViewModel.orderLineResponse(
                                                status = "ACCEPTED",
                                                id = line.id!!,
                                                isAll = false
                                            )
                                        }
                                        }
                                        Row (
                                            modifier = Modifier.weight(1f)
                                        ){

                                        ButtonSubmit(
                                            labelValue = "Refuse",
                                            color = Color.Red,
                                            enabled = true,
                                        ) {
                                            shoppingViewModel.orderLineResponse(
                                                status = "REFUSED",
                                                id = line.id!!,
                                                isAll = false
                                            )
                                        }
                                        }
                                    }
                                } else {
                                    ButtonSubmit(
                                        labelValue = "Cancel",
                                        color = Color.Red,
                                        enabled = true
                                    ) {
                                        shoppingViewModel.orderLineResponse(
                                            status = "CANCELLED",
                                            id = line.id!!,
                                            isAll = false
                                        )
                                    }
                                }
                            }

                            Status.ACCEPTED.toString() -> {
                                if(line.purchaseorder?.company?.id != companyViewModel.myCompany.id){
                                Text(text = "${line.purchaseorder?.company?.name} has accepted ${line.article?.article?.libelle} order")
                                }
                                else{
                                Text(text = "you have accepted ${line.article?.article?.libelle} order from ${line.purchaseorder?.person?.username ?: line.purchaseorder?.client?.name}")
                                }
                            }

                            Status.CANCELLED.toString() -> {
                                if(line.purchaseorder?.company?.id == companyViewModel.myCompany.id){
                                Text(text = "${line.purchaseorder?.person?.username ?: line.purchaseorder?.client?.name} has cancelled ${line.article?.article?.libelle} order")
                                }else{
                                Text(text = "you have cancelled ${line.article?.article?.libelle} order to ${line.purchaseorder?.person?.username ?: line.purchaseorder?.client?.name}")
                                }
                            }

                            Status.REFUSED.toString() -> {
                                if(line.purchaseorder?.company?.id != companyViewModel.myCompany.id){
                                Text(text = "${line.purchaseorder?.company?.name} has refused ${line.article?.article?.libelle} order")
                                }
                                else{
                                Text(text = "you have refused ${line.article?.article?.libelle} order from ${line.purchaseorder?.person?.username ?: line.purchaseorder?.client?.name}")
                                }
                            }
                        }
                    }
                }
            }
        }

//    Surface(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(3.dp)
//    ) {
//        LazyColumn(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            items(shoppingViewModel.allMyOrdersLine){line ->
//                Text(text = line.quantity.toString())
//                Text(text = line.delivery.toString())
//                Text(text = line.article?.libelle?:"")
//                Text(text = line.purchaseorder?.orderNumber.toString())
//                when(line.status){
//                    Status.INWAITING.toString() ->
//                        {
//                        if (
//                            line.purchaseorder?.company?.id == companyViewModel.myCompany.id
//                            ) {
//                            ButtonSubmit(
//                                labelValue = "Accept",
//                                color = Color.Green,
//                                enabled = true
//                            ) {
//                                shoppingViewModel.orderLineResponse(status = "ACCEPTED", line.id!!)
//                            }
//                            ButtonSubmit(labelValue = "Refuse", color = Color.Red, enabled = true) {
//                                shoppingViewModel.orderLineResponse(status = "REFUSED", line.id!!)
//                            }
//                        } else {
//                            ButtonSubmit(labelValue = "Cancel", color = Color.Red, enabled = true) {
//                                shoppingViewModel.orderLineResponse(status = "CANCELLED", line.id!!)
//                            }
//                        }
//                    }
//                    Status.ACCEPTED.toString() -> {
//                        Text(text =  (line.purchaseorder?.company?.name) +" has accepted ${line.article?.libelle} order")
//                            }
//                    Status.CANCELLED.toString() -> {
//                        Text(text = (if(line.purchaseorder?.person?.username != "") line.purchaseorder?.person?.username else line.purchaseorder!!.client?.name) +" has cancelled ${line.article?.libelle} order")
//                    }
//                    Status.REFUSED.toString() -> {
//                        Text(text = line.purchaseorder?.company?.name +" has refused ${line.article?.libelle} order")
//                    }
//                }
//
//            }
//        }
//    }
    }
}