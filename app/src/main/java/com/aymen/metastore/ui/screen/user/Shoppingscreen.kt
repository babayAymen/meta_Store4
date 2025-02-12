package com.aymen.metastore.ui.screen.user

import android.R.string
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.entity.model.PurchaseOrder
import com.aymen.metastore.model.entity.model.PurchaseOrderLine
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.Status
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.InvoiceViewModel
import com.aymen.store.model.repository.ViewModel.ShoppingViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.DividerComponent
import com.aymen.metastore.ui.component.LodingShape
import com.aymen.metastore.ui.component.OrderShow
import com.aymen.metastore.ui.component.ShowFeesDialog
import com.aymen.metastore.ui.screen.admin.AddInvoiceScreen
import com.aymen.metastore.ui.screen.admin.OrderScreen
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.Date

    import android.provider.Settings;
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.withStyle
import com.aymen.metastore.R
import com.aymen.metastore.model.Enum.InvoiceDetailsType
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.ui.component.NotImage
import com.aymen.metastore.ui.component.ShoppingDialog
import com.aymen.metastore.ui.component.ShowImage
import com.aymen.metastore.ui.screen.admin.stringToLocalDateTime
import com.aymen.metastore.util.ADD_INVOICE
import com.aymen.metastore.util.CART
import com.aymen.metastore.util.IMAGE_URL_COMPANY
import com.aymen.metastore.util.INVOICE
import com.aymen.metastore.util.INVOICES
import com.aymen.metastore.util.NOT_DELIVERED
import com.aymen.metastore.util.ORDER
import com.aymen.metastore.util.ORDER_LINE
import com.aymen.metastore.util.ORDER_LINE_DETAILS
import com.aymen.metastore.util.SHOPPING

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShoppingScreen() {
    val context = LocalContext.current
    val shoppingViewModel : ShoppingViewModel = hiltViewModel()
    val appViewModel : AppViewModel = hiltViewModel()
    val sharedViewModel : SharedViewModel = hiltViewModel()
    val invoiceViewModel : InvoiceViewModel = hiltViewModel()
    val myCompany by sharedViewModel.company.collectAsStateWithLifecycle()
    val accountType by sharedViewModel.accountType.collectAsStateWithLifecycle()
    val show by appViewModel.show
    val view by appViewModel.view
    sharedViewModel.setOrderCountNotification(true)
    var order by remember {
        mutableStateOf(PurchaseOrder())
    }
    var showFeesDialog by remember { mutableStateOf(false) }
    var showShoppingDialog by remember { mutableStateOf(false) }
    var orderId = 0L
    var isAll by remember {
        mutableStateOf(false)
    }
    var priceTotal by remember {
        mutableStateOf(BigDecimal.ZERO)
    }
    var invoicesEnbaled by remember {
        mutableStateOf(true)
    }
    var ordersEnbaled by remember {
        mutableStateOf(true)
    }
    var cartEnbaled by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = view) {
        when(view){
            CART ->{
                cartEnbaled = false
                invoicesEnbaled = true
                ordersEnbaled = true
            }
            INVOICES ->{
                cartEnbaled = true
                invoicesEnbaled = false
                ordersEnbaled = true
            }
            ORDER ->{
                cartEnbaled = true
                invoicesEnbaled = true
                ordersEnbaled = false
            }
        }
    }
    when(show) {
        ADD_INVOICE -> {
            invoiceViewModel.setInvoiceMode(InvoiceMode.VERIFY)
            AddInvoiceScreen()
        }
        ORDER_LINE -> OrderScreen(invoiceViewModel, appViewModel, sharedViewModel)
        ORDER_LINE_DETAILS -> PurchaseOrderDetailsScreen(order, shoppingViewModel)
        SHOPPING -> {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
            ) {
                Column (
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp)
                        ) {
                            Button(
                                onClick = {
                                    appViewModel.updateView(CART)
                                },
                                colors = ButtonColors(
                                    contentColor = Color.White,
                                    containerColor = Color.Black,
                                    disabledContentColor = Color.Black,
                                    disabledContainerColor = Color(0xFFEBEBEB)
                                ),
                                enabled = cartEnbaled,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(id = R.string.cart),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 2.dp)
                        ) {
                            Button(
                                onClick = {
                                    appViewModel.updateView(ORDER)
                                },
                                colors = ButtonColors(
                                    contentColor = Color.White,
                                    containerColor = Color.Black,
                                    disabledContentColor = Color.Black,
                                    disabledContainerColor = Color(0xFFEBEBEB)
                                ),
                                enabled = ordersEnbaled,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(id = R.string.orders),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        if(accountType != AccountType.COMPANY)
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp)
                        ) {
                            Button(
                                onClick = {
                                    invoicesEnbaled = false
                                    appViewModel.updateView(INVOICES)
                                },
                                colors = ButtonColors(
                                    contentColor = Color.White,
                                    containerColor = Color.Black,
                                    disabledContentColor = Color.Black,
                                    disabledContainerColor = Color(0xFFEBEBEB)
                                ),
                                enabled = invoicesEnbaled,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(id = R.string.invoices),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }

                        }
                    }
                    when (view) {
                        CART -> {
                            val allMyOrdersLineDetails = shoppingViewModel.allMyOrdersLineDetails.collectAsLazyPagingItems()
                            if(allMyOrdersLineDetails.itemCount != 0){
                                shoppingViewModel.orderArray = emptyList()
                                shoppingViewModel.orderArray = allMyOrdersLineDetails.itemSnapshotList.items
                            }
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    LazyColumn(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                            .padding(5.dp)
                                    ) {
                                        item {
                                            Text(
                                                text = stringResource(id = R.string.your_cart),
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            Spacer(modifier = Modifier.height(25.dp))
                                        }
                                        itemsIndexed(shoppingViewModel.orderArray) { index, it ->
                                            OrderShow(it, shoppingViewModel, myCompany.id?:0) { delete , id ->
                                                if (delete) {
                                                    shoppingViewModel.removeOrderById(
                                                        index = index,
                                                        false
                                                    )
                                                } else {
                                                    orderId = id
                                                    showShoppingDialog = true
                                                }
                                            }
                                            if (showShoppingDialog && it.article?.id == orderId) {
                                                ShoppingDialog(
                                                    it.article,
                                                    label = "",
                                                    isOpen = true,
                                                    shoppingViewModel = shoppingViewModel,
                                                    quantity = it.quantity,
                                                    commentaire = it.comment
                                                ) {
                                                    showShoppingDialog = false
                                                }
                                            }
                                        }
                                    }
                                    PurchaseCard(shoppingViewModel, myCompany.id?:0) { delivery, totPrice , status ->
                                        val ids = shoppingViewModel.orderArray.map {order ->
                                            order.id?:0
                                        }
                                        priceTotal = totPrice
                                        if (delivery) {
                                            if(status != Status.ACCEPTED) {
                                                if (totPrice < BigDecimal(30))
                                                    shoppingViewModel.sendOrder()
                                                else
                                                    showFeesDialog = true
                                            }else{
                                                shoppingViewModel.orderLineResponse(
                                                    status = Status.ACCEPTED,
                                                    ids = ids,
                                                    totPrice.toDouble(),
                                                    order.deliveryCode != null
                                                )
                                            }
                                        } else {
                                            if(totPrice != BigDecimal.ZERO){
                                                if(status == Status.REFUSED)
                                                    shoppingViewModel.orderLineResponse(
                                                        status = Status.REFUSED,
                                                        ids = ids,
                                                        null,
                                                        order.deliveryCode != null
                                                    )
                                                else
                                                    shoppingViewModel.orderLineResponse(
                                                        status = Status.CANCELLED,
                                                        ids = ids,
                                                        totPrice.toDouble(),
                                                        order.deliveryCode != null
                                                    )

                                            }
                                        }
                                        shoppingViewModel.returnAllMyMony()
                                    }
                                    if (showFeesDialog) {
                                        ShowFeesDialog(isOpen = true) { submitfees ->
                                            if (submitfees) {
                                                shoppingViewModel.sendOrder()
                                            }
                                            showFeesDialog = false
                                        }
                                    }
                                }
                            }
                        }

                        INVOICES -> {
                            val myInvoicesAccepted = invoiceViewModel.invoicesAsClient.collectAsLazyPagingItems()
                            LazyColumn {
                                item {
                                    Text(
                                        text = stringResource(id = R.string.your_invoices),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                    items(count = myInvoicesAccepted.itemCount,
                                        key = myInvoicesAccepted.itemKey { it.id!! }
                                    ) { index ->
                                        val invoice = myInvoicesAccepted[index]
                                        if (invoice != null) {
                                            InvoiceCard(
                                                invoice = invoice
                                            ){
                                                invoiceViewModel.asProvider = false
                                                invoiceViewModel.setInvoice(invoice)
                                                invoiceViewModel.clientType = accountType
                                                invoiceViewModel.discount = invoice.discount ?: 0.0
                                                invoiceViewModel.invoiceType = invoice.type!!
                                                invoiceViewModel.setInvoiceMode(InvoiceMode.VERIFY)
                                                appViewModel.updateShow(ADD_INVOICE)
                                            }
                                        }
                                    }

                            }
                        }

                        ORDER -> {
                            val allMyOrdersNotAccepted =
                                shoppingViewModel.allMyOrdersNotAccepted.collectAsLazyPagingItems()
                            LazyColumn {
                                item {
                                    Text(
                                        text = stringResource(id = R.string.your_order),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                items(count = allMyOrdersNotAccepted.itemCount,
                                key = allMyOrdersNotAccepted.itemKey{it.id!!})
                                { index: Int ->
                                    val orderLine = allMyOrdersNotAccepted[index]
                                    if (orderLine != null) {
                                        OrderCard(order =orderLine ){
                                            appViewModel.updateView(CART)
                                            shoppingViewModel.getAllMyOrdersLine(orderLine.id!!)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
//                                        Text(text =
//                                        if (orderLine.company?.id == myCompany.id && accountType == AccountType.COMPANY) stringResource(
//                                            id = R.string.your_order,
//                                            orderLine.person?.username ?: orderLine.client?.name
//                                            ?: ""
//                                        )
//                                        else stringResource(
//                                            id = R.string.your_sent_order,
//                                            orderLine.company?.name ?: ""
//                                        ),
//                                            modifier = Modifier.clickable {
//                                                order = orderLine
//                                                shoppingViewModel.Order = orderLine
//                                                appViewModel.updateShow(ORDER_LINE_DETAILS)
//                                            }
//                                        )
//                                        Text(
//                                            text = date.toString(),
//                                            style = TextStyle(fontSize = 8.sp)
//                                        )
//                Column(
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Row(// all my orders those not send yet
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Column {
//                            if (shoppingViewModel.orderArray.isNotEmpty()) {
//                                LazyColumn(
//                                    modifier = Modifier.fillMaxWidth()
//                                ) {
//                                    itemsIndexed(shoppingViewModel.orderArray) { index, it ->
//                                        Row {
//                                            Row(
//                                                modifier = Modifier.weight(0.8f)
//                                            ) {
//                                                OrderShow(order = it)
//                                            }
//                                            Row(
//                                                modifier = Modifier.weight(0.1f)
//                                            ) {
//                                                IconButton(onClick = {
//                                                    if(it.delivery == true && (BigDecimal(it.article?.sellingPrice!!).multiply(BigDecimal(it.quantity!!)))<= BigDecimal(30)){
//                                                        restBalnace = BigDecimal(myCompany.balance!!).subtract(BigDecimal(it.article.sellingPrice!!).multiply(BigDecimal(it.quantity))).setScale(2, RoundingMode.HALF_UP)
//                                                        showFeesDialog = true
//                                                    }
//                                                }) {
//                                                    Icon(
//                                                        imageVector = Icons.Default.Check,
//                                                        contentDescription = stringResource(id = R.string.send),
//                                                        tint = Color.Magenta
//                                                    )
//                                                }
//                                            }
//                                            if(showFeesDialog){
//                                                ShowFeesDialog(isOpen = true) {submitfees ->
//                                                    if(submitfees){
//                                                            val balance = restBalnace.subtract(BigDecimal((3)))
//                                                        if(!isAll)
//                                                            shoppingViewModel.sendOrder(index, balance)
//                                                        else
//                                                            shoppingViewModel.sendOrder(-1, balance)
//                                                    }else{
//                                                        showFeesDialog = false
//                                                    }
//                                                }
//                                            }
//                                            Row(
//                                                modifier = Modifier.weight(0.1f)
//                                            ) {
//                                                IconButton(onClick = {
//                                                    shoppingViewModel.removeOrderById(index, true)
//                                                }) {
//                                                    Icon(
//                                                        imageVector = Icons.Default.Remove,
//                                                        contentDescription = stringResource(id = R.string.remove),
//                                                        tint = Color.Red
//                                                    )
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                                Column {
//
//                                    Row {
//                                        Row(
//                                            modifier = Modifier.weight(1f)
//                                        ) {
//                                            ButtonSubmit(
//                                                labelValue = stringResource(id = R.string.cancel_all),
//                                                color = Color.Red,
//                                                enabled = true
//                                            ) {
//                                                shoppingViewModel.returnAllMyMony()
//                                            }
//                                        }
//                                        Row(
//                                            modifier = Modifier.weight(1f)
//                                        ) {
//                                            ButtonSubmit(
//                                                labelValue = stringResource(id = R.string.send_all),
//                                                color = Color.Green,
//                                                enabled = true
//                                            ) {
//                                                if (shoppingViewModel.delivery && shoppingViewModel.cost <= BigDecimal(
//                                                        30
//                                                    )
//                                                ) {
//                                                    restBalnace =
//                                                        BigDecimal(myCompany.balance!!).subtract(
//                                                            shoppingViewModel.cost
//                                                        ).setScale(2, RoundingMode.HALF_UP)
//                                                    isAll = true
//                                                    showFeesDialog = true
//                                                }
//                                            }
//                                        }
//                                    }
//                                    DividerComponent()
//                                }
//                            }
//                        }
//                    }
//                    Row( // all my invoices those do not accepted
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        LazyColumn {
//
//                            if(accountType == AccountType.USER) {
//                                items(count = myInvoicesAccepted?.itemCount!!,
//                                    key = myInvoicesAccepted.itemKey{it.id!!}
//                                    ) { index ->
//                                    val invoice = myInvoicesAccepted[index]
//                                    if(invoice != null && invoice.status == Status.INWAITING) {
//                                        InvoiceCard(
//                                            invoice = invoice,
//                                            appViewModel = appViewModel,
//                                            invoiceViewModel = invoiceViewModel,
//                                            asProvider = false
//                                        )
//                                    }
//                                }
//                            }
//                            item {
//                              //  DividerComponent()
//                            }
//                        }
//                    }ebêtkb^tkbù^rtkbù^prtkbbprktbùprtkbùprtbùktbùùk'tbùpke'
//                    Row( // all my orders those do not accepted
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        LazyColumn(
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            items(count = allMyOrdersNotAccepted.itemCount,
//                                key = allMyOrdersNotAccepted.itemKey{it.id!!})
//                            { index: Int ->
//                                val orderLine = allMyOrdersNotAccepted[index]
//                                if (orderLine != null) {
//                                    val date = orderLine.createdDate?.let {
//                                    val dateTime = LocalDateTime.parse(orderLine.createdDate)
//                                      dateTime.toLocalDate()
//                                    }
//                                    Text(text =
//                                    if (orderLine.company?.id == myCompany.id && accountType == AccountType.COMPANY) stringResource(
//                                        id = R.string.your_order,orderLine.person?.username ?: orderLine.client?.name?:"")
//                                    else stringResource(id = R.string.your_sent_order,orderLine.company?.name?:""),
//                                        modifier = Modifier.clickable {
//                                            order = orderLine
//                                            shoppingViewModel.Order = orderLine
//                                            appViewModel.updateShow(ORDER_LINE_DETAILS)
//                                        }
//                                    )
//                                    Text(
//                                        text = date.toString(),
//                                        style = TextStyle(fontSize = 8.sp)
//                                    )
//                                }
//                                DividerComponent()
//                            }
//                        }
//                    }
//                    Row (// all my invoices those are accepted and orders also accepted
//                        modifier = Modifier.fillMaxWidth()
//                    ){
//                        LazyColumn {
//                            if(accountType == AccountType.USER) {
//                                items(count = myInvoicesAccepted?.itemCount!!,
//                                    key = myInvoicesAccepted.itemKey { it.id!! }) { index ->
//                                    val invoice = myInvoicesAccepted[index]
//                                    if(invoice != null && invoice.status == Status.ACCEPTED) {
//                                        InvoiceCard(
//                                            invoice = invoice,
//                                            appViewModel = appViewModel,
//                                            invoiceViewModel = invoiceViewModel,
//                                            asProvider = false
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
            }
        }
    }
}

@Composable
fun PurchaseCard(shoppingViewModel: ShoppingViewModel, myCompanyId : Long ,
                 onSubmit : (Boolean, BigDecimal, Status) -> Unit) {
    val orderArray = shoppingViewModel.orderArray
    val totItem = shoppingViewModel.orderArray.size
    var totArticle = BigDecimal.ZERO
    var providerId = -1L
    var orderId : Long? = 0L
    shoppingViewModel.orderArray.forEach {
            val price = BigDecimal(it.quantity!!)
                .multiply(BigDecimal(it.article?.sellingPrice!!))
                .setScale(2, RoundingMode.HALF_UP)
            val tvaRate = BigDecimal(it.article.article?.tva ?: 0.0)
            val tvaAmount = price.multiply(tvaRate).divide(BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)

            totArticle = totArticle.add(price.add(tvaAmount))
    }
    var deliveryFee = BigDecimal.ZERO
    if(totArticle< BigDecimal(30) && orderArray.isNotEmpty() && shoppingViewModel.delivery)
        deliveryFee = BigDecimal(3)
    val totalPrice = totArticle.add(deliveryFee).setScale(2,RoundingMode.HALF_UP)
    var asProvider = false
    if(totalPrice.compareTo(BigDecimal.ZERO) != 0){
        orderId = shoppingViewModel.orderArray[0].purchaseorder?.id
        providerId = shoppingViewModel.orderArray[0].article?.company?.id!!
        if(myCompanyId == providerId && orderId != null){
            asProvider = true
        }
    }
    Card(
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .padding(8.dp)
            .background(Color.Transparent),
        colors = CardColors(containerColor = Color.White, contentColor = Color.Black, disabledContentColor = Color.Black, disabledContainerColor = Color.Black)
    ) {
        Column (
            modifier = Modifier.padding(8.dp)
        ){
            Text( text = buildAnnotatedString {
                append(stringResource(id = R.string.tot_item))
                append(" ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(totItem.toString())
                }
                append(" ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(stringResource(id = R.string.item))
                }
            },
                fontSize = 14.sp)
            CustomSpacer()
            Text( text = buildAnnotatedString {
                append(stringResource(id = R.string.delivery_fees))
                append(" ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(deliveryFee.toString())
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(stringResource(id = R.string.dt))
                }
            },
                fontSize = 14.sp)
            CustomSpacer()
            Text( text = buildAnnotatedString {
                append(stringResource(id = R.string.total_price))
                append(" ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(totalPrice.toString())
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(stringResource(id = R.string.dt))
                }
            },
                fontSize = 14.sp)
            CustomSpacer()
            Row {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Button(
                        onClick = {
                            onSubmit(true, totalPrice, if(asProvider) Status.ACCEPTED else Status.NULL)
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonColors(contentColor = Color.White , containerColor = Color(0xFF01BC59), disabledContentColor =  Color.Black, disabledContainerColor =  Color.Black)
                    ) {
                        Text(text = if(asProvider) stringResource(id = R.string.accept) else stringResource(id = R.string.purchase), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }
                }
                Row (
                    modifier = Modifier.weight(1f)
                ){
                    Button(onClick = {
                        onSubmit(false, if(orderId == null) BigDecimal.ZERO else totalPrice, if(asProvider) Status.REFUSED else Status.CANCELLED)
                    },
                        modifier = Modifier
                            .fillMaxWidth(),
                        border = (BorderStroke(1.dp, Color.Black)),
                        shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(contentColor = Color.Black , containerColor = Color.White, disabledContentColor =  Color.Black, disabledContainerColor =  Color.Black)
                    ) {
                        Text(text = stringResource(id = R.string.delete_all), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomSpacer() {
    Spacer(modifier = Modifier.height(8.dp))
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderCard(order : PurchaseOrder, onClicked :() -> Unit) {
    Card (
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .padding(16.dp)
            .clickable {
                onClicked()
            },
        colors = CardColors(contentColor = Color.Black, containerColor = Color.White, disabledContentColor = Color.Black , disabledContainerColor = Color.Black)
    ){
        Column(
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            Text( text = buildAnnotatedString {
                append(stringResource(id = R.string.order_number))
                append(" : ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(order.orderNumber.toString())
                }
            },
                fontSize = 14.sp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (order.company?.logo != null) {
                    ShowImage(
                        image = String.format(
                            IMAGE_URL_COMPANY,
                            order.company.logo,
                            order.company.user?.id
                        )
                    )
                } else {
                    NotImage()
                }
                Row {

                    order.company?.let { it1 ->
                        Text(
                            text = it1.name,
                            fontSize = 14.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "verification account",
                        tint = if (order.company?.metaSeller == true) Color.Green else Color.Cyan,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
            DividerComponent()
            Text( text = buildAnnotatedString {
                append(stringResource(id = R.string.amount))
                append(" : ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(order.prix_order_tot.toString())
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(stringResource(id = R.string.dt))
                }
            },
                fontSize = 14.sp)
            Text( text = buildAnnotatedString {
                append(stringResource(id = R.string.date))
                append(" : ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(stringToLocalDateTime(order.createdDate))
                }
            },
                fontSize = 14.sp)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InvoiceCard(invoice : Invoice, onClicked :() -> Unit) {
    Card (
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .padding(16.dp)
            .clickable {
                onClicked()
            },
        colors = CardColors(contentColor = Color.Black, containerColor = Color.White, disabledContentColor = Color.Black , disabledContainerColor = Color.Black)
    ){
        Column(
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (invoice.provider?.logo != null) {
                    ShowImage(
                        image = String.format(
                            IMAGE_URL_COMPANY,
                            invoice.provider?.logo,
                            invoice.provider?.user?.id
                        )
                    )
                } else {
                    NotImage()
                }
                Row {

                    invoice.provider?.let { it1 ->
                        Text(
                            text = it1.name,
                            fontSize = 14.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "verification account",
                        tint = if (invoice.provider?.metaSeller == true) Color.Green else Color.Cyan,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
            Box (
                modifier = Modifier.fillMaxWidth()
            ){
            Text( text = buildAnnotatedString {
                append(stringResource(id = R.string.invoice_code))
                append(" : ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(invoice.code.toString())
                }
            },
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text( text = buildAnnotatedString {
                append(stringResource(id = R.string.status))
                append(" : ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(invoice.status?.name?.lowercase())
                }
            },
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
            }
            DividerComponent()
            Text( text = buildAnnotatedString {
                append(stringResource(id = R.string.amount))
                append(" : ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(invoice.prix_invoice_tot.toString())
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(stringResource(id = R.string.dt))
                }
            },
                fontSize = 14.sp)
            Text( text = buildAnnotatedString {
                append(stringResource(id = R.string.date))
                append(" : ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(stringToLocalDateTime(invoice.createdDate))
                }
            },
                fontSize = 14.sp)
        }
    }
}















