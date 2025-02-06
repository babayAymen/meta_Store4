package com.aymen.metastore.ui.screen.user

import android.util.Log
import androidx.collection.mutableLongListOf
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.R
import com.aymen.metastore.model.entity.model.PurchaseOrder
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.Status
import com.aymen.metastore.model.repository.ViewModel.CompanyViewModel
import com.aymen.store.model.repository.ViewModel.ShoppingViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun PurchaseOrderDetailsScreen(order : PurchaseOrder, shoppingViewModel: ShoppingViewModel) {

    val companyViewModel: CompanyViewModel = hiltViewModel()
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val context = LocalContext.current
    var hasWaitingStatus by remember {
        mutableStateOf(true)
    }
    var price by remember {
        mutableStateOf(BigDecimal.ZERO)
    }
    val ids: MutableList<Long> = emptyList<Long>().toMutableList()
    val allMyOrdersLineDetails = shoppingViewModel.allMyOrdersLineDetails.collectAsLazyPagingItems()
    val myCompany by sharedViewModel.company.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = allMyOrdersLineDetails.itemCount) {
        shoppingViewModel.getAllMyOrdersLine(order.id!!)
        if (allMyOrdersLineDetails.itemCount != 0) {
            val snapshot = allMyOrdersLineDetails.itemSnapshotList.items
            price = BigDecimal(snapshot[0].purchaseorder?.prix_order_tot!!)
            snapshot.forEach { line ->
                if (line.status != Status.INWAITING) {
                    hasWaitingStatus = false
                }
                ids += line.id!!
            Log.e("testorder","list ids $ids")
            }
        }

    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp)
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                if (hasWaitingStatus) {
                    if (order.company?.id == myCompany.id) {
                        Row {
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                ButtonSubmit(
                                    labelValue = stringResource(id = R.string.accept_all),
                                    color = Color.Green,
                                    enabled = true,
                                ) {
                                    hasWaitingStatus = false
                                    shoppingViewModel.orderLineResponse(
                                        status = Status.ACCEPTED,
                                        ids = ids,
                                        price.toDouble(),
                                        order.deliveryCode != null
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.weight(1f)
                            ) {
                                ButtonSubmit(
                                    labelValue = stringResource(id = R.string.refuse_all),
                                    color = Color.Red,
                                    enabled = true,
                                ) {
                                    hasWaitingStatus = false
                                    shoppingViewModel.orderLineResponse(
                                        status = Status.REFUSED,
                                        ids = ids,
                                        null,
                                        order.deliveryCode != null
                                    )
                                }
                            }
                        }
                    } else {
                        Row {
                            ButtonSubmit(
                                labelValue = stringResource(id = R.string.cancel_all),
                                color = Color.Red,
                                enabled = true
                            ) {
                                hasWaitingStatus = false
                                shoppingViewModel.orderLineResponse(
                                    status = Status.CANCELLED,
                                    ids = ids,
                                    price.toDouble(),
                                    order.deliveryCode != null
                                )
                            }
                        }
                    }
                }
            }
            items(count = allMyOrdersLineDetails.itemCount,
                key = allMyOrdersLineDetails.itemKey { it.id!! }
            ) { index: Int ->
                val line = allMyOrdersLineDetails[index]
                if (line != null) {

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
                                                labelValue = stringResource(id = R.string.accept),
                                                color = Color.Green,
                                                enabled = true,
                                            ) {
                                                price = BigDecimal(line.prixArticleTot!!).add(BigDecimal(line.totTva!!))
                                                shoppingViewModel.orderLineResponse(
                                                    status = Status.ACCEPTED,
                                                    ids = listOf(line.id!!),
                                                    price.toDouble(),
                                                    order.deliveryCode != null

                                                )
                                            }
                                        }
                                        Row(
                                            modifier = Modifier.weight(1f)
                                        ) {

                                            ButtonSubmit(
                                                labelValue = stringResource(id = R.string.refuse),
                                                color = Color.Red,
                                                enabled = true,
                                            ) {
                                                shoppingViewModel.orderLineResponse(
                                                    status = Status.REFUSED,
                                                    ids = listOf(line.id!!),
                                                    null,
                                                    order.deliveryCode != null
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    ButtonSubmit(
                                        labelValue = stringResource(id = R.string.cancel),
                                        color = Color.Red,
                                        enabled = true
                                    ) {
                                        price = BigDecimal(line.prixArticleTot!!).add(BigDecimal(line.totTva!!))
                                        shoppingViewModel.orderLineResponse(
                                            status = Status.CANCELLED,
                                            ids = listOf(line.id!!),
                                            price.toDouble(),
                                            order.deliveryCode != null
                                        )
                                    }
                                }
                            }

                            Status.ACCEPTED -> {
                                if (line.purchaseorder?.company?.id != companyViewModel.myCompany.id) {
                                    Text(
                                        text = stringResource(
                                            id = R.string.his_accept_order,
                                            line.article?.company?.name ?: "",
                                            line.article?.article?.libelle ?: ""
                                        )
                                    )
                                } else {
                                    Text(
                                        text = stringResource(
                                            id = R.string.your_accept_order,
                                            line.article?.article?.libelle ?: "",
                                            line.purchaseorder?.person?.username
                                                ?: line.purchaseorder?.client?.name ?: ""
                                        )
                                    )
                                }
                            }

                            Status.CANCELLED -> {
                                if (line.purchaseorder?.company?.id == companyViewModel.myCompany.id) {
                                    Text(
                                        text = stringResource(
                                            id = R.string.his_cancel_order,
                                            line.purchaseorder?.person?.username
                                                ?: line.purchaseorder?.client?.name ?: "",
                                            line.article?.article?.libelle ?: ""
                                        )
                                    )
                                } else {
                                    Text(
                                        text = stringResource(
                                            id = R.string.your_cancelled_order,
                                            line.article?.article?.libelle ?: "",
                                            line.purchaseorder?.person?.username
                                                ?: line.purchaseorder?.client?.name ?: ""
                                        )
                                    )
                                }
                            }

                            Status.REFUSED -> {
                                if (line.purchaseorder?.company?.id != companyViewModel.myCompany.id) {
                                    Text(
                                        text = stringResource(
                                            id = R.string.his_refuse_order,
                                            line.purchaseorder?.company?.name ?: "",
                                            line.article?.article?.libelle ?: ""
                                        )
                                    )
                                } else {
                                    Text(
                                        text = stringResource(
                                            id = R.string.your_refuse_order,
                                            line.article?.article?.libelle ?: "",
                                            line.purchaseorder?.person?.username
                                                ?: line.purchaseorder?.client?.name ?: ""
                                        )
                                    )
                                }
                            }

                            Status.NULL -> {}
                            null -> TODO()
                        }
                    }
                }
            }
        }
    }
}