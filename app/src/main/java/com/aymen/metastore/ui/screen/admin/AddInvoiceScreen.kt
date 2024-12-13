package com.aymen.metastore.ui.screen.admin

import android.os.Build
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AddToHomeScreen
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.R
import com.aymen.metastore.model.Enum.InvoiceDetailsType
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.dependencyInjection.BASE_URL
import com.aymen.metastore.model.entity.model.Company
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.InvoiceViewModel
import com.aymen.metastore.ui.component.ArticleDialog
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.ClientDialog
import com.aymen.metastore.ui.component.DiscountTextField
import com.aymen.metastore.ui.component.ShowImage
import com.aymen.metastore.ui.component.ShowPaymentDailog
import com.aymen.metastore.ui.component.ShowQuantityDailog
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
    val currentDateTime = LocalDateTime.now()
    val formattedDate = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) // HH:mm:ss
    LaunchedEffect(key1 = Unit) {
        if (invoiceMode == InvoiceMode.CREATE) {
        invoiceViewModel.getLastInvoiceCode()
        }else{
            invoiceViewModel.getInvoiceDetails()
        }
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
    var isShowQuantityDailog by remember {
        mutableStateOf(false)
    }
    var showArticleDialog by remember {
        mutableStateOf(false)
    }
    var article by remember {
        mutableStateOf(ArticleCompany())
    }

    val commandsLine by invoiceViewModel.commandLine.collectAsStateWithLifecycle()

    val ordersLineInvoice = invoiceViewModel.ordersLine.collectAsLazyPagingItems()
    val commandLineInvoice = invoiceViewModel.commandLineInvoice.collectAsLazyPagingItems()
    val invoice = if(invoiceViewModel.invoice.type == InvoiceDetailsType.ORDER_LINE) ordersLineInvoice.itemSnapshotList.items.firstOrNull()?.invoice
        else
        commandLineInvoice.itemSnapshotList.items.firstOrNull()?.invoice
    DisposableEffect(key1 = Unit) {
        onDispose {
            invoiceViewModel.remiseOrderLineToZero()
            tottva = BigDecimal.ZERO
            totprice = BigDecimal.ZERO
            totgen = BigDecimal.ZERO
        }
    }
    LaunchedEffect(key1 = ordersLineInvoice.itemCount, key2 = commandLineInvoice.itemCount) {
        for(i in 0 until commandLineInvoice.itemCount){
            val commandLine = commandLineInvoice[i]
            if(commandLine != null){
            invoiceViewModel.addCommandLine(commandLine)
            }
        }
        for(i in 0 until ordersLineInvoice.itemCount){
            val orderLine = ordersLineInvoice[i]
            if(orderLine != null) {
                val tvaInvoice = invoice?.tot_tva_invoice ?: 0.0
                val priceArticleTot = invoice?.prix_article_tot ?: 0.0
                tottva = tottva.add(BigDecimal(tvaInvoice))
                totprice = totprice.add(BigDecimal(priceArticleTot))
                totgen = totprice.add(tottva)
                totprice = totprice.setScale(2, RoundingMode.HALF_UP)
                tottva = tottva.setScale(2, RoundingMode.HALF_UP)
                totgen = totgen.setScale(2, RoundingMode.HALF_UP)
            }
        }
    }
    LaunchedEffect(key1 = commandsLine,key2 = invoiceViewModel.discount) {
         tottva = BigDecimal.ZERO
         totprice = BigDecimal.ZERO
         totgen = BigDecimal.ZERO
        commandsLine.forEach {
            tottva = tottva.add(BigDecimal(it.totTva ?: 0.0))
            totprice = totprice.add(BigDecimal(it.prixArticleTot))
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
        mutableStateOf(PaymentStatus.NOT_PAID)
    }
    var discount by remember {
        mutableStateOf("")
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
                                contentDescription = "send"
                            )
                        }
                        IconButton(
                            onClick = {
                                invoiceViewModel.startScan {
                                    if(it == null){
                                    Toast.makeText(context, "this barcode not found", Toast.LENGTH_SHORT).show()
                                    }else{
                                        article = it
                                        isShowQuantityDailog = true
                                    }
                                }
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.AddToHomeScreen,
                                contentDescription = "Favorite"
                            )
                        }
                        if(isShowQuantityDailog){
                            ShowQuantityDailog(article , true,invoiceViewModel, false){
                                    isShowQuantityDailog = false
                            }
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
                                val invoicee = Invoice().copy()
                                    invoicee.rest = mony.toDouble()
                                    invoicee.paid = paid
                                for (x in commandsLine){
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
                    Text(text = if (clientType == AccountType.COMPANY) clientCompany.name else clientUser.username!!)
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
                                                        invoiceViewModel.article = commandLine.article!!
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
                                    labelValue = discount,
                                    label = "discount",
                                    singleLine = true,
                                    maxLine = 1,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    enabled = true
                                ) {
                                    if (it.matches(Regex("^[0-9]*[,.]?[0-9]*$"))) {
                                        val normalizedInput = it.replace(',', '.')
                                        discount = normalizedInput
                                        invoiceViewModel.discount = if (normalizedInput.startsWith(".") && normalizedInput.endsWith(".")) {
                                            0.0
                                        }else if (normalizedInput.endsWith(".")) {
                                            normalizedInput.let { inp ->
                                                if (inp.toDouble() % 1.0 == 0.0) inp.toDouble() else 0.0
                                            }
                                        } else {
                                            normalizedInput.toDoubleOrNull() ?: 0.0
                                        }
                                    }
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
            if(invoice != null) {
                Column {
                    Row {
                        Row(modifier = Modifier.weight(1f)) {
                            Column {
                                Text(text = invoice.provider?.name!!)
                                invoice.provider.phone?.let { Text(text = it) }
                                invoice.provider.address?.let { Text(text = it) }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 2.dp), Arrangement.End
                        ) {
                            Column {
                                ShowImage(
                                    image = "${BASE_URL}werehouse/image/${invoice.provider?.logo}/company/${invoice.provider?.user?.id}"
                                )
                                Text(text = invoice.provider?.email ?: "")
                                invoice.provider?.matfisc?.let { Text(text = it) }
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
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            ) {
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
                                IconButton(
                                    onClick = {
                                        invoiceViewModel.startScan {
                                            if(it == null){
                                                Toast.makeText(context, "this barcode not found", Toast.LENGTH_SHORT).show()
                                            }else{
                                                article = it
                                                isShowQuantityDailog = true
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.AddToHomeScreen,
                                        contentDescription = "Favorite"
                                    )
                                }
                                if (showPaymentDailog) {
                                    ShowPaymentDailog(totgen, openDailog = true) { mony, payed ->
                                        commandsLine[0].invoice?.rest =
                                            mony.toDouble()
                                        if (payed) {
                                            commandsLine[0].invoice?.paid =
                                                PaymentStatus.PAID
                                        } else {
                                            if (mony != BigDecimal.ZERO) {
                                                commandsLine[0].invoice?.paid =
                                                    PaymentStatus.INCOMPLETE
                                            } else {
                                                commandsLine[0].invoice?.paid =
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
                    Row {
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
                                    Text(text = it.username!!)
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
                                        itemsIndexed(commandsLine) { index, commandLine ->
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
                                                    invoiceViewModel.article = commandLine.article!!
                                                    invoiceViewModel.commandLineDto = commandLine
                                                    ArticleDialog(update = true, openDialo = true) {
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
                                    labelValue = discount,
                                    label = "discount",
                                    singleLine = true,
                                    maxLine = 1,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    enabled = true
                                ) {
                                    if (it.matches(Regex("^[0-9]*[,.]?[0-9]*$"))) {
                                        val normalizedInput = it.replace(',', '.')
                                        discount = normalizedInput
                                        invoiceViewModel.discount = if (normalizedInput.startsWith(".") && normalizedInput.endsWith(".")) {
                                            0.0
                                        }else if (normalizedInput.endsWith(".")) {
                                            normalizedInput.let { inp ->
                                                if (inp.toDouble() % 1.0 == 0.0) inp.toDouble() else 0.0
                                            }
                                        } else {
                                            normalizedInput.toDoubleOrNull() ?: 0.0
                                        }
                                    }
                                }
                                Text(text = "total tva: $tottva Dt")
                                Text(text = "total price: $totprice Dt")
                                Text(text = "total general: $totgen Dt")
                            }
                        }
                    }
                }
        }
        InvoiceMode.VERIFY ->{
                Column {
                    Row {
                        Row(modifier = Modifier.weight(1f)) {
                            Column {
                                Text(text = invoice?.provider?.name?:"")
                                invoice?.provider?.phone?.let { Text(text = it) }
                                invoice?.provider?.address?.let { Text(text = it) }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 2.dp), Arrangement.End
                        ) {
                            Column {
                                ShowImage(
                                    image = "${BASE_URL}werehouse/image/${invoice?.provider?.logo}/company/${invoice?.provider?.user?.id}"
                                )
                                Text(text = invoice?.provider?.email ?: "")
                                invoice?.provider?.matfisc?.let { Text(text = it) }
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
                                text = invoice?.code.toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                            Text(
                                text = "invoice date: ${invoice?.createdDate}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                            if(invoice?.status == Status.INWAITING){
                                if(invoice.provider?.id != myCompany.id){
                            ButtonSubmit(labelValue = "Accept", color = Color.Green, enabled = true) {
                                invoiceViewModel.accepteInvoice(invoice.id!! , Status.ACCEPTED)
                            }
                                }else{
                                    Text("waiting for accept")
                                }
                            }
                        }
                    }
                    Row {
                        Row {
                            Column {
                                invoice?.client?.let {
                                    Text(text = it.name)
                                    invoiceViewModel.clientCompany = it
                                    invoiceViewModel.clientType = AccountType.COMPANY
                                }
                                invoice?.person?.let {
                                    Text(text = it.username!!)
                                    invoiceViewModel.clientUser = it
                                    invoiceViewModel.clientType = AccountType.USER
                                }
                                invoice?.client?.phone?.let { Text(text = it) }
                                invoice?.person?.phone?.let { Text(text = it) }
                                invoice?.person?.address?.let { Text(text = it) }
                                invoice?.client?.address?.let { Text(text = it) }
                            }
                        }
                        Row {
                            Column {

                            }
                        }
                    }
                }
                if (commandsLine.isNotEmpty() || ordersLineInvoice.itemCount !=0) {

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
                                        items(count = ordersLineInvoice.itemCount,
                                            key = ordersLineInvoice.itemKey { it.id!! }) { index ->
                                            val order = ordersLineInvoice[index]
                                            if(order != null){
                                            invoiceViewModel.discount =
                                                order.invoice?.discount ?: 0.0
                                            Row(
                                                modifier = Modifier
                                                    .padding(3.dp)
                                                    .width(800.dp)
                                            ) {
                                                Text(
                                                    text = order.article?.article?.libelle!!,
                                                    modifier = Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                order.article.article!!.code?.let {
                                                    Text(
                                                        text = it,
                                                        modifier = Modifier
                                                            .padding(end = 3.dp)
                                                            .weight(1f)
                                                            .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                    )
                                                }
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
                                                    text = order.article.article!!.tva.toString(),
                                                    modifier = Modifier
                                                        .padding(end = 3.dp)
                                                        .weight(1f)
                                                        .background(if (index % 2 == 0) Color.Gray else Color.LightGray)
                                                )
                                                Text(
                                                    text = order.article.sellingPrice!!.toString(),
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
