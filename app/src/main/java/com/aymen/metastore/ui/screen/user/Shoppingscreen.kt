package com.aymen.store.ui.screen.user

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.metastore.model.entity.converterRealmToApi.mapPurchaseOrderLineDtoToRealm
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.ShoppingViewModel
import com.aymen.store.ui.component.ButtonSubmit
import com.aymen.store.ui.component.DividerTextComponent
import com.aymen.store.ui.component.LodingShape
import com.aymen.store.ui.component.OrderShow
import com.aymen.store.ui.screen.admin.OrderScreen
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShoppingScreen() {
    val shoppingViewModel : ShoppingViewModel = hiltViewModel()
    val appViewModel : AppViewModel = hiltViewModel()
    val sharedViewModel : SharedViewModel = hiltViewModel()
    val myCompany by sharedViewModel.company.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = Unit) {
        shoppingViewModel.getAllMyOrders()
    }
    DisposableEffect(Unit) {
        onDispose {

        }
    }
    val context = LocalContext.current
    val show by appViewModel.show
    when(show){
        "orderLine" -> OrderScreen()
        "orderLineDetails" -> PurchaseOrderDetailsScreen(shoppingViewModel = shoppingViewModel)
        else -> {


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp)
    ) {
        Column (
            modifier =  Modifier.fillMaxWidth()
        ){
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {

                if (shoppingViewModel.orderArray.isNotEmpty()) {

                    LazyColumn(

                        modifier =  Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(shoppingViewModel.orderArray) {index , it ->
                            Row {
                                Row(
                                    modifier = Modifier.weight(0.8f)
                                ) {

                            OrderShow(order = mapPurchaseOrderLineDtoToRealm(it))
                                }
                                Row (
                                    modifier = Modifier.weight(0.1f)
                                ){
                                    IconButton(onClick = {
                                        shoppingViewModel.sendOrder(index)
                                    }) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = "send", tint = Color.Magenta)
                                    }
                                }
                                Row (
                                    modifier = Modifier.weight(0.1f)
                                ){
                                    IconButton(onClick = {
                                        shoppingViewModel.removeOrderById(index)
                                    }) {
                                        Icon(imageVector = Icons.Default.Remove, contentDescription = "remove", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                    DividerTextComponent()
                    Row {
                        Row(
                            modifier = Modifier.weight(1f)
                        ) {
                            ButtonSubmit(labelValue = "Cancel All", color = Color.Red, enabled = true) {
                                shoppingViewModel.orderArray = emptyList()
                            }
                        }
                        Row (
                            modifier = Modifier.weight(1f)
                        ){
                    ButtonSubmit(labelValue = "Send All", color = Color.Green, enabled = true) {
                        shoppingViewModel.sendOrder(-1)
                        }
                    }
                    }
                }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn (
                    modifier =  Modifier.fillMaxWidth()
                ) {

                    if (shoppingViewModel.isLoading) {
                        item {
                            LodingShape()
                        }
                    }
                        items(shoppingViewModel.allMyOrders) {
                            val dateTime = LocalDateTime.parse(it.createdDate)
                            val date = dateTime.toLocalDate()
                            Text(text =
                            if (it.company?.id == myCompany.id) "you have an order from ${it.person?.username ?: it.client?.name}" else "you have sent an order to ${it.company?.name}",
                                modifier = Modifier.clickable {
                                    shoppingViewModel.Order = it
                                    appViewModel.updateShow("orderLineDetails")
                                }
                            )
                            Text(text = date.toString(),
                                style = TextStyle(fontSize = 8.sp)
                            )
                        }

                }
            }
        }
    }
        }
    }
}