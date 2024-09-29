package com.aymen.store.ui.screen.admin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aymen.metastore.model.Enum.InvoiceDetailsType
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.dependencyInjection.BASE_URL
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.entity.api.InvoiceDto
import com.aymen.store.model.entity.converterRealmToApi.mapArticleCompanyToRealm
import com.aymen.store.model.entity.realm.Invoice
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.InvoiceViewModel
import com.aymen.store.ui.component.ArticleDialog
import com.aymen.store.ui.component.ClientDialog
import com.aymen.store.ui.component.DiscountTextField
import com.aymen.store.ui.component.LodingShape
import com.aymen.store.ui.component.ShowImage
import com.aymen.store.ui.component.ShowPaymentDailog
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddInvoiceScreen(invoiceMode : InvoiceMode) {
    val context = LocalContext.current
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val invoiceViewModel: InvoiceViewModel = hiltViewModel()
    val appViewModel : AppViewModel = hiltViewModel()
    val myCompany by sharedViewModel.company.collectAsStateWithLifecycle()
    val clientCompany = invoiceViewModel.clientCompany
    val clientUser = invoiceViewModel.clientUser
    val clientType = invoiceViewModel.clientType
    val isLoading = invoiceViewModel.isLoading
    val invoice = invoiceViewModel.invoice
    val currentDateTime = LocalDateTime.now()
    val formattedDate = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) // HH:mm:ss
    LaunchedEffect(key1 = Unit) {
        invoiceViewModel.getLastInvoiceCode()
    }
    var totgen by remember {
        mutableStateOf(BigDecimal.ZERO)
    }
    var tottva by remember {
        mutableStateOf(BigDecimal.ZERO)
    }
    var totprice by remember {
        mutableStateOf(BigDecimal.ZERO)
    }
    var showClientDialog by remember {
        mutableStateOf(false)
    }
    var showPaymentDailog by remember {
        mutableStateOf(false)
    }
    var showArticleDialog by remember {
        mutableStateOf(false)
    }

    val commandsLine = invoiceViewModel.commandLineDtos
    val ordersLine by invoiceViewModel.ordersLineArray.collectAsStateWithLifecycle()
    DisposableEffect(key1 = Unit) {
        onDispose {
            invoiceViewModel.commandLineDtos = emptyList()
            invoiceViewModel._ordersLineArray.value = emptyList()
            invoiceViewModel.discount = 0.0
            tottva = BigDecimal.ZERO
            totprice = BigDecimal.ZERO
            totgen = BigDecimal.ZERO
        }
    }
    LaunchedEffect(key1 = commandsLine, key2 = ordersLine,key3 = invoiceViewModel.discount) {
         tottva = BigDecimal.ZERO
         totprice = BigDecimal.ZERO
         totgen = BigDecimal.ZERO

        commandsLine.forEach {
            tottva = tottva.add(BigDecimal(it.totTva ?: 0.0))
            totprice = totprice.add(BigDecimal(it.prixArticleTot))
            totgen = totprice.add(tottva)
        }

        ordersLine.forEach {
            val tvaInvoice = it.invoice?.tot_tva_invoice ?: 0.0
            val priceArticleTot = it.invoice?.prix_article_tot ?: 0.0

            tottva = tottva.add(BigDecimal(tvaInvoice))
            totprice = totprice.add(BigDecimal(priceArticleTot))
            totgen = totprice.add(tottva)
        }

        val discount = BigDecimal(invoiceViewModel.discount).divide(BigDecimal(100))
        val totalWithDiscount = (tottva.add(totprice)).subtract((tottva.add(totprice)).multiply(discount))
        totprice = totprice.setScale(2, RoundingMode.HALF_UP)
        tottva = tottva.setScale(2, RoundingMode.HALF_UP)
        totgen = totalWithDiscount.setScale(2, RoundingMode.HALF_UP)
    }
    var articleIndex by remember {
        mutableIntStateOf(-1)
    }
    var paid by remember {
        mutableStateOf(PaymentStatus.NOT_PAID)//test push git :kn
    }

    when(invoiceMode){
        InvoiceMode.CREATE -> {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    Column {
                        Text(text = myCompany.name)
                        myCompany.phone?.let { Text(text = it) }
                        myCompany.address?.let { Text(text = it) }
                    }
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 2.dp), Arrangement.End
                ) {
                    Column {
                        ShowImage(
                            image = "${BASE_URL}werehouse/image/${myCompany.logo}/company/${myCompany.user?.id}"
                        )
                        Text(text = myCompany.email ?: "")
                        myCompany.matfisc?.let { Text(text = it) }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                Column {
                    Text(
                        text = invoiceViewModel.lastInvoiceCode.toString(),
                       modifier = Modifier
                           .fillMaxWidth()
                           .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                    Text(text = "invoice date: $formattedDate",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    ) {
                        ArticleDialog(update = false,openDialo = false){
                            showArticleDialog = false
                        }
                        IconButton(
                            onClick = {
                                showPaymentDailog = true
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Favorite"
                            )
                        }
                        if(showPaymentDailog){
                            ShowPaymentDailog(totgen,openDailog = true){mony, payed ->
                                paid = if(payed) {
                                    PaymentStatus.PAID
                                }else{
                                    if(mony != BigDecimal.ZERO){
                                        PaymentStatus.INCOMPLETE
                                    }else {
                                        PaymentStatus.NOT_PAID
                                    }
                                }
                                val invoicee = InvoiceDto().copy()
                                    invoicee.rest = mony.toDouble()
                                    invoicee.paid = paid
                                for (x in invoiceViewModel.commandLineDtos){
                                    x.invoice = invoicee
                                }
                                invoiceViewModel.addInvoice()
                                appViewModel.updateShow("invoice")
                            }
                        }
                    }
                }
            }
            Row {
                Column(
                    modifier = Modifier.clickable {
                        showClientDialog = true
                    }
                ) {
                    if (showClientDialog) {
                        ClientDialog(update = true , openDialoge = true) {
                            showClientDialog = false
                        }
                    }
                    Text(text = if (clientType == AccountType.COMPANY) clientCompany.name else clientUser.username)
                    Text(
                        text = if (clientType == AccountType.COMPANY) clientCompany.phone
                            ?: "" else clientUser.phone ?: ""
                    )
                    Text(
                        text = if (clientType == AccountType.COMPANY) clientCompany.address
                            ?: "" else clientUser.address ?: ""
                    )
                }
            }
            Column {
                        LazyRow {
                            item {
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .padding(3.dp)
                                            .width(800.dp)
                                    ) {
                                        Text(
                                            "Label",
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier
                                                .padding(end = 10.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "Code", modifier = Modifier
                                                .padding(end = 10.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "Qte", modifier = Modifier
                                                .padding(end = 10.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "U", modifier = Modifier
                                                .padding(end = 10.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "TVA", modifier = Modifier
                                                .padding(end = 10.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "P/U", modifier = Modifier
                                                .padding(end = 10.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "Tot Tva", modifier = Modifier
                                                .padding(end = 10.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "P Tot Ar", modifier = Modifier
                                                .padding(end = 10.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "Discount", modifier = Modifier
                                                .padding(end = 10.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                    }
                                    LazyColumn {
                                        itemsIndexed(commandsLine){index , commandLine ->
                                                Row(
                                                    modifier = Modifier
                                                        .padding(3.dp)
                                                        .width(800.dp)
                                                        .clickable {
                                                            articleIndex = index
                                                            showArticleDialog = true
                                                        }
                                                ) {
                                                    if (showArticleDialog && index == articleIndex) {
                                                        invoiceViewModel.article = mapArticleCompanyToRealm(commandLine.article!!)
                                                        invoiceViewModel.commandLineDto = commandLine
                                                        ArticleDialog(update = true , openDialo = true) {
                                                            showArticleDialog = false
                                                        }
                                                    }
                                                    Text(
                                                        commandLine.article?.article?.libelle!!,
                                                        Modifier
                                                            .padding(end = 10.dp)
                                                            .weight(1f)
                                                            .background(color = Color.LightGray)
                                                    )
                                                    Text(
                                                        commandLine.article?.article?.code ?: "",
                                                        Modifier
                                                            .padding(end = 10.dp)
                                                            .weight(1f)
                                                            .background(color = Color.LightGray)
                                                    )
                                                    Text(
                                                        commandLine.quantity.toString(),
                                                        Modifier
                                                            .padding(end = 10.dp)
                                                            .weight(1f)
                                                            .background(color = Color.LightGray)
                                                    )
                                                    Text(
                                                        commandLine.article?.unit.toString(),
                                                        Modifier
                                                            .padding(end = 10.dp)
                                                            .weight(1f)
                                                            .background(color = Color.LightGray)
                                                    )
                                                    Text(
                                                        commandLine.article?.article?.tva.toString(),
                                                        Modifier
                                                            .padding(end = 10.dp)
                                                            .weight(1f)
                                                            .background(color = Color.LightGray)
                                                    )
                                                    Text(
                                                        commandLine.article?.sellingPrice.toString(),
                                                        Modifier
                                                            .padding(end = 10.dp)
                                                            .weight(1f)
                                                            .background(color = Color.LightGray)
                                                    )
                                                    Text(
                                                        commandLine.totTva.toString(),
                                                        Modifier
                                                            .padding(end = 10.dp)
                                                            .weight(1f)
                                                            .background(color = Color.LightGray)
                                                    )
                                                    Text(
                                                        commandLine.prixArticleTot.toString(),
                                                        Modifier
                                                            .padding(end = 10.dp)
                                                            .weight(1f)
                                                            .background(color = Color.LightGray)
                                                    )
                                                    Text(
                                                        commandLine.discount.toString(),
                                                        Modifier
                                                            .padding(end = 10.dp)
                                                            .weight(1f)
                                                            .background(color = Color.LightGray)
                                                    )
                                                }

                                        }
                                    }

                                }
                            }
                        }
                        Row {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.End
                            ) {
                                DiscountTextField(
                                    labelValue = if (invoiceViewModel.discount == 0.0) "" else invoiceViewModel.discount.toString(),
                                    label = "discount",
                                    singleLine = true,
                                    maxLine = 1,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    enabled = true
                                ) {
                                    invoiceViewModel.discount = it.toDouble()
                                }
                                Text(text = "total tva: $tottva Dt")
                                Text(text = "total price: $totprice Dt")
                                Text(text = "total general: $totgen Dt")
                            }
                        }


            }
        }

    }
    }
        InvoiceMode.UPDATE -> {
        LaunchedEffect(key1 = Unit) {
            invoiceViewModel.getAllCommandLineByInvoiceId()
        }
            if (isLoading) {
                LodingShape()
            }else{
                Column {
                    Row {
                        Row(modifier = Modifier.weight(1f)) {
                            Column {
                                Text(text = invoice.provider?.name!!)
                                invoice.provider!!.phone?.let { Text(text = it) }
                                invoice.provider!!.address?.let { Text(text = it) }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 2.dp), Arrangement.End
                        ) {
                            Column {
                                ShowImage(
                                    image = "${BASE_URL}werehouse/image/${invoice.provider!!.logo}/company/${invoice.provider!!.user?.id}"
                                )
                                Text(text = invoice.provider!!.email ?: "")
                                invoice.provider!!.matfisc?.let { Text(text = it) }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    ) {
                        Column {
                            Text(text = invoice.code.toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally))
                            Text(text = "invoice date: ${invoice.createdDate}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally))
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)) {
                                ArticleDialog(update = false, openDialo = false) {
                                    showArticleDialog = false
                                }
                                IconButton(
                                    onClick = {
                                        showPaymentDailog = true
                                    }
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Favorite"
                                    )
                                }
                                if(showPaymentDailog){
                                    ShowPaymentDailog(totgen,openDailog = true){mony, payed ->
                                        invoiceViewModel.commandLineDtos[0].invoice?.rest = mony.toDouble()
                                        if(payed) {
                                            invoiceViewModel.commandLineDtos[0].invoice?.paid = PaymentStatus.PAID
                                        }else{
                                            if(mony != BigDecimal.ZERO){
                                                invoiceViewModel.commandLineDtos[0].invoice?.paid = PaymentStatus.INCOMPLETE
                                            }else {
                                                invoiceViewModel.commandLineDtos[0].invoice?.paid =
                                                    PaymentStatus.NOT_PAID
                                            }
                                        }
                                    invoiceViewModel.addInvoice()
                                        appViewModel.updateShow("invoice")
                                    }
                                }
                            }
                        }
                    }
                    Row{
                       Row {
                           Column(
                               modifier = Modifier
                                   .clickable {
                                       showClientDialog = true
                                   }
                           ) {
                               if (showClientDialog) {
                                   ClientDialog(update = false, openDialoge = true) {
                                       showClientDialog = false
                                   }
                               }
                               invoice.client?.let {
                                   Text(text = it.name)
                                   invoiceViewModel.clientCompany = it
                                   invoiceViewModel.clientType = AccountType.COMPANY
                               }
                               invoice.person?.let {
                                   Text(text = it.username)
                                   invoiceViewModel.clientUser = it
                                   invoiceViewModel.clientType = AccountType.USER
                               }
                               invoice.client?.phone?.let { Text(text = it) }
                               invoice.person?.phone?.let { Text(text = it) }
                               invoice.person?.address?.let { Text(text = it) }
                               invoice.client?.address?.let { Text(text = it) }
                           }
                       }
                        Row {
                            Column {

                            }
                        }
                    }
                }
                if(commandsLine.isNotEmpty()) {
                    Column {
                        LazyRow {
                            item {
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .padding(3.dp)
                                            .width(800.dp)
                                    ) {
                                        Text(
                                            "Label",
                                            modifier = Modifier
                                                .padding(end = 3.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "Code", modifier = Modifier
                                                .padding(end = 3.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "Qte", modifier = Modifier
                                                .padding(end = 3.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "U", modifier = Modifier
                                                .padding(end = 3.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "TVA", modifier = Modifier
                                                .padding(end = 3.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "P/U", modifier = Modifier
                                                .padding(end = 3.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "Tot Tva", modifier = Modifier
                                                .padding(end = 3.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "P Tot Ar", modifier = Modifier
                                                .padding(end = 3.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "Discount", modifier = Modifier
                                                .padding(end = 3.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                    }
                                    LazyColumn {
                                        itemsIndexed(invoiceViewModel.commandLineDtos) { index, commandLine ->
                                            Row(
                                                modifier = Modifier
                                                    .padding(3.dp)
                                                    .width(800.dp)
                                                    .clickable {
                                                        articleIndex = index
                                                        showArticleDialog = true
                                                    }
                                            ) {
                                                if (showArticleDialog && index == articleIndex) {
                                                    invoiceViewModel.article = mapArticleCompanyToRealm(commandLine.article!!)
                                                    invoiceViewModel.commandLineDto = commandLine
                                                    ArticleDialog(update = true , openDialo = true) {
                                                        showArticleDialog = false
                                                    }
                                                }
                                                Text(
                                                    commandLine.article?.article?.libelle!!,
                                                    Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    commandLine.article?.article?.code ?: "",
                                                    Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    commandLine.quantity.toString(),
                                                    Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    commandLine.article?.unit.toString(),
                                                    Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    commandLine.article?.article?.tva.toString(),
                                                    Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    commandLine.article?.sellingPrice.toString(),
                                                    Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    commandLine.totTva.toString(),
                                                    Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    commandLine.prixArticleTot.toString(),
                                                    Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    text = commandLine.discount.toString(),
                                                    Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.End)
                                .padding(3.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.End
                            ) {
                                DiscountTextField(
                                    labelValue = if (invoiceViewModel.discount == 0.0) "" else invoiceViewModel.discount.toString(),
                                    label = "discount",
                                    singleLine = true,
                                    maxLine = 1,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    enabled = true
                                ) {
                                    invoiceViewModel.discount =
                                        it.toDouble()
                                }
                                Text(text = "total tva: $tottva Dt")
                                Text(text = "total price: $totprice Dt")
                                Text(text = "total general: $totgen Dt")
                            }
                        }
                    }
                }
            }
        }
        InvoiceMode.VERIFY ->{
            LaunchedEffect(key1 = Unit) {
                if(invoiceViewModel.invoice.type == InvoiceDetailsType.COMMAND_LINE.toString()){
                    invoiceViewModel.getAllCommandLineByInvoiceId()
                }else{
                    invoiceViewModel.getAllOrdersLineByInvoiceId()
                }
            }
            if (isLoading) {
                LodingShape()
            }else {
                Column {
                    Row {
                        Row(modifier = Modifier.weight(1f)) {
                            Column {
                                Text(text = invoice.provider?.name!!)
                                invoice.provider!!.phone?.let { Text(text = it) }
                                invoice.provider!!.address?.let { Text(text = it) }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 2.dp), Arrangement.End
                        ) {
                            Column {
                                ShowImage(
                                    image = "${BASE_URL}werehouse/image/${invoice.provider!!.logo}/company/${invoice.provider!!.user?.id}"
                                )
                                Text(text = invoice.provider!!.email ?: "")
                                invoice.provider!!.matfisc?.let { Text(text = it) }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    ) {
                        Column {
                            Text(
                                text = invoice.code.toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                            Text(
                                text = "invoice date: ${invoice.createdDate}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }
                    Row {
                        Row {
                            Column {
                                invoice.client?.let {
                                    Text(text = it.name)
                                    invoiceViewModel.clientCompany = it
                                    invoiceViewModel.clientType = AccountType.COMPANY
                                }
                                invoice.person?.let {
                                    Text(text = it.username)
                                    invoiceViewModel.clientUser = it
                                    invoiceViewModel.clientType = AccountType.USER
                                }
                                invoice.client?.phone?.let { Text(text = it) }
                                invoice.person?.phone?.let { Text(text = it) }
                                invoice.person?.address?.let { Text(text = it) }
                                invoice.client?.address?.let { Text(text = it) }
                            }
                        }
                        Row {
                            Column {

                            }
                        }
                    }
                }
                if (commandsLine.isNotEmpty() || ordersLine.isNotEmpty()) {

                    Column {
                        LazyRow {
                            item {
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .padding(3.dp)
                                            .width(800.dp)
                                    ) {
                                        Text(
                                            "Label",
                                            modifier = Modifier
                                                .padding(end = 3.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "Code", modifier = Modifier
                                                .padding(end = 3.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "Qte", modifier = Modifier
                                                .padding(end = 3.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "U", modifier = Modifier
                                                .padding(end = 3.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "TVA", modifier = Modifier
                                                .padding(end = 3.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "P/U", modifier = Modifier
                                                .padding(end = 3.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "Tot Tva", modifier = Modifier
                                                .padding(end = 3.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "P Tot Ar", modifier = Modifier
                                                .padding(end = 3.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                        Text(
                                            "Discount", modifier = Modifier
                                                .padding(end = 3.dp)
                                                .weight(1f)
                                                .background(color = Color.LightGray)
                                        )
                                    }
                                    LazyColumn {
                                        itemsIndexed(commandsLine) { index, commandLine ->
                                            Row(
                                                modifier = Modifier
                                                    .padding(3.dp)
                                                    .width(800.dp)
                                            ) {

                                                Text(
                                                    commandLine.article?.article?.libelle!!,
                                                    Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    commandLine.article?.article?.code ?: "",
                                                    Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    commandLine.quantity.toString(),
                                                    Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    commandLine.article?.unit.toString(),
                                                    Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    commandLine.article?.article?.tva.toString(),
                                                    Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    commandLine.article?.sellingPrice.toString(),
                                                    Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    commandLine.totTva.toString(),
                                                    Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    commandLine.prixArticleTot.toString(),
                                                    Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    text = commandLine.discount.toString(),
                                                    Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                            }
                                        }
                                    }
                                    LazyColumn {
                                        itemsIndexed(ordersLine){index , order ->
                                            invoiceViewModel.discount = order.invoice?.discount?:0.0
                                            Row(
                                                modifier = Modifier
                                                    .padding(3.dp)
                                                    .width(800.dp)
                                            ) {
                                                Text(
                                                    text = order.article.article.libelle,
                                                    modifier = Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    text = order.article.article.code,
                                                    modifier = Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    text = order.quantity.toString(),
                                                    modifier = Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    text = order.article.unit.toString(),
                                                    modifier = Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    text = order.article.article.tva.toString(),
                                                    modifier = Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    text = order.article.sellingPrice.toString(),
                                                    modifier = Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    text = order.invoice?.tot_tva_invoice.toString(),
                                                    modifier = Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    text = order.invoice?.prix_article_tot.toString(),
                                                    modifier = Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    text = order.invoice?.discount.toString(),
                                                    modifier = Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.End)
                                .padding(3.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.End
                            ) {
                                DiscountTextField(
                                    labelValue = invoiceViewModel.discount.toString(),
                                    label = "discount",
                                    singleLine = true,
                                    maxLine = 1,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    enabled = false
                                ) {

                                }
                                Text(text = "total tva: $tottva Dt")
                                Text(text = "total price: $totprice Dt")
                                Text(text = "total general: $totgen Dt")
                            }
                        }
                    }
                }
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
fun stringToLocalDateTime(dateTimeString: String, format: String = "yyyy-MM-dd HH:mm:ss"): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern(format)
    return LocalDateTime.parse(dateTimeString, formatter)
}

@Composable
fun Item(item : String, index : Int, modifier : Modifier) {
    Row {
Text(text = item,
    Modifier
        .weight(1f)
        .padding(end = 3.dp)
        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
)
    }
}
