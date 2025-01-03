package com.aymen.metastore.ui.screen.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.model.entity.model.PurchaseOrder
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.Status
import com.aymen.metastore.model.repository.ViewModel.CompanyViewModel
import com.aymen.store.model.repository.ViewModel.ShoppingViewModel
import com.aymen.metastore.ui.component.ButtonSubmit

@Composable
fun PurchaseOrderDetailsScreen(order : PurchaseOrder, shoppingViewModel: ShoppingViewModel) {

    val companyViewModel: CompanyViewModel = hiltViewModel()
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val context = LocalContext.current
    var hasWaitingStatus by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(Unit) {
        shoppingViewModel.getAllMyOrdersLine(order.id!!)

    }
    val allMyOrdersLineDetails = shoppingViewModel.allMyOrdersLineDetails.collectAsLazyPagingItems()
    val myCompany by sharedViewModel.company.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp)
    ) {

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                if(order.company?.id == myCompany.id) {
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
                                            status = Status.ACCEPTED,
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
                                            status = Status.REFUSED,
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
                                        status = Status.CANCELLED,
                                        id = shoppingViewModel.Order.id!!,
                                        isAll = true
                                    )
                                    hasWaitingStatus = false
                                }
                            }
                        }
                    }
                }
                items(count = allMyOrdersLineDetails.itemCount,
                    key = allMyOrdersLineDetails.itemKey { it.id!! }
                ) {index : Int ->
                    val line = allMyOrdersLineDetails[index]
                    if(line != null) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(text = line.quantity.toString())
                            Text(text = line.delivery.toString())
                            Text(text = line.article?.article?.libelle ?: "")
                            Text(text = line.purchaseorder?.orderNumber.toString())
                            line.comment?.let { Text(text = it) }
                            Text(text = line.status.toString())
                            when (line.status) {
                                Status.INWAITING -> {
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
                                                        status = Status.ACCEPTED,
                                                        id = line.id!!,
                                                        isAll = false
                                                    )
                                                }
                                            }
                                            Row(
                                                modifier = Modifier.weight(1f)
                                            ) {

                                                ButtonSubmit(
                                                    labelValue = "Refuse",
                                                    color = Color.Red,
                                                    enabled = true,
                                                ) {
                                                    shoppingViewModel.orderLineResponse(
                                                        status = Status.REFUSED,
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
                                                status = Status.CANCELLED,
                                                id = line.id!!,
                                                isAll = false
                                            )
                                        }
                                    }
                                }

                                Status.ACCEPTED -> {
                                    if (line.purchaseorder?.company?.id != companyViewModel.myCompany.id) {
                                        Text(text = "${line.article?.company?.name} has accepted ${line.article?.article?.libelle} order")
                                    } else {
                                        Text(text = "you have accepted ${line.article?.article?.libelle} order from ${line.purchaseorder?.person?.username ?: line.purchaseorder?.client?.name}")
                                    }
                                }

                                Status.CANCELLED -> {
                                    if (line.purchaseorder?.company?.id == companyViewModel.myCompany.id) {
                                        Text(text = "${line.purchaseorder?.person?.username ?: line.purchaseorder?.client?.name} has cancelled ${line.article?.article?.libelle} order")
                                    } else {
                                        Text(text = "you have cancelled ${line.article?.article?.libelle} order to ${line.purchaseorder?.person?.username ?: line.purchaseorder?.client?.name}")
                                    }
                                }

                                Status.REFUSED -> {
                                    if (line.purchaseorder?.company?.id != companyViewModel.myCompany.id) {
                                        Text(text = "${line.purchaseorder?.company?.name} has refused ${line.article?.article?.libelle} order")
                                    } else {
                                        Text(text = "you have refused ${line.article?.article?.libelle} order from ${line.purchaseorder?.person?.username ?: line.purchaseorder?.client?.name}")
                                    }
                                }

                                null -> TODO()
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
//                Text(text = line.purchaseOrder?.orderNumber.toString())
//                when(line.status){
//                    Status.INWAITING.toString() ->
//                        {
//                        if (
//                            line.purchaseOrder?.company?.id == companyViewModel.myCompany.id
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
//                        Text(text =  (line.purchaseOrder?.company?.name) +" has accepted ${line.article?.libelle} order")
//                            }
//                    Status.CANCELLED.toString() -> {
//                        Text(text = (if(line.purchaseOrder?.person?.username != "") line.purchaseOrder?.person?.username else line.purchaseOrder!!.client?.name) +" has cancelled ${line.article?.libelle} order")
//                    }
//                    Status.REFUSED.toString() -> {
//                        Text(text = line.purchaseOrder?.company?.name +" has refused ${line.article?.libelle} order")
//                    }
//                }
//
//            }
//        }
//    }
    }
}