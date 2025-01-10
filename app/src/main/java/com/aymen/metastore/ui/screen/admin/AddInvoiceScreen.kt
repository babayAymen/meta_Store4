package com.aymen.metastore.ui.screen.admin

import android.content.Context
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AddToHomeScreen
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.R
import com.aymen.metastore.model.Enum.InvoiceDetailsType
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.Enum.InvoiceType
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.CashModel
import com.aymen.metastore.model.entity.model.CommandLine
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.model.Payment
import com.aymen.metastore.model.entity.model.PurchaseOrderLine
import com.aymen.metastore.model.entity.model.User
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.InvoiceViewModel
import com.aymen.metastore.model.repository.ViewModel.PaymentViewModel
import com.aymen.metastore.ui.component.ArticleDialog
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.ClientDialog
import com.aymen.metastore.ui.component.DiscountTextField
import com.aymen.metastore.ui.component.InputTextField
import com.aymen.metastore.ui.component.ShowImage
import com.aymen.metastore.ui.component.ShowPaymentDailog
import com.aymen.metastore.ui.component.ShowQuantityDailog
import com.aymen.metastore.ui.component.notImage
import com.aymen.metastore.util.BASE_URL
import com.aymen.metastore.util.IMAGE_URL_COMPANY
import com.aymen.store.ui.screen.user.generateOrderPDF
import com.aymen.store.ui.screen.user.generatePDF
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
    val paymentViewModel: PaymentViewModel = hiltViewModel()
    val appViewModel: AppViewModel = hiltViewModel()
    val myCompany by sharedViewModel.company.collectAsStateWithLifecycle()
    val clientCompany = invoiceViewModel.clientCompany
    val clientUser = invoiceViewModel.clientUser
    val clientType = invoiceViewModel.clientType
    val currentDateTime = LocalDateTime.now()
    val formattedDate = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        if (invoiceMode == InvoiceMode.CREATE) {
            invoiceViewModel.getLastInvoiceCode()
        } else {
            invoiceViewModel.getInvoiceDetails()
            paymentViewModel.getPaymentHystoricByInvoiceId(invoiceId = invoiceViewModel.invoice.id!!)
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
    var showPaymentDailog by remember {
        mutableStateOf(false)
    }
    var showArticleDialog by remember {
        mutableStateOf(false)
    }
    var showPaymentDialog by remember {
        mutableStateOf(false)
    }

    val commandsLine by invoiceViewModel.commandLine.collectAsStateWithLifecycle()
    val ordersLineInvoice = invoiceViewModel.ordersLine.collectAsLazyPagingItems()
    val commandLineInvoice = invoiceViewModel.commandLineInvoice.collectAsLazyPagingItems()
    val paymentHistoric = paymentViewModel.paymentHistoric.collectAsLazyPagingItems()
    var invoice =
        if (invoiceViewModel.invoice.type == InvoiceDetailsType.ORDER_LINE)
            ordersLineInvoice.itemSnapshotList.items.firstOrNull()?.invoice
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
        if (invoiceMode == InvoiceMode.UPDATE) {
            for (i in 0 until commandLineInvoice.itemCount) {
                val commandLine = commandLineInvoice[i]
                if (commandLine != null) {
                    invoiceViewModel.addCommandLine(commandLine)
                }
            }
            for (i in 0 until ordersLineInvoice.itemCount) {
                val orderLine = ordersLineInvoice[i]
                if (orderLine != null) {
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
    }
    LaunchedEffect(key1 = commandsLine, key2 = invoiceViewModel.discount) {
        if (commandsLine.isNotEmpty()) {
            tottva = BigDecimal.ZERO
            totprice = BigDecimal.ZERO
            totgen = BigDecimal.ZERO
            commandsLine.forEach {
                tottva = tottva.add(BigDecimal(it.totTva ?: 0.0))
                totprice = totprice.add(BigDecimal(it.prixArticleTot))
                totgen = totprice.add(tottva)
            }

            val discount = BigDecimal(invoiceViewModel.discount).divide(BigDecimal(100))
            val totalWithDiscount =
                (tottva.add(totprice)).subtract((tottva.add(totprice)).multiply(discount))
            totprice = totprice.setScale(2, RoundingMode.HALF_UP)
            tottva = tottva.setScale(2, RoundingMode.HALF_UP)
            totgen = totalWithDiscount.setScale(2, RoundingMode.HALF_UP)
        }
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

    val labels = listOf(
        R.string.label,
        R.string.code,
        R.string.quantity,
        R.string.unit,
        R.string.tva,
        R.string.prix_unit,
        R.string.tot_tva,
        R.string.prix_article_tot,
        R.string.discount
    )
    when (invoiceMode) {
        InvoiceMode.CREATE -> {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
            ) {
                LazyColumn {
                    item {
                        MyCompanyDetails(myCompany)
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
                                Text(
                                    text = stringResource(
                                        id = R.string.invoice_date,
                                        formattedDate
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                                FeatureIcons(
                                    invoiceViewModel,
                                    context,
                                    invoiceMode,
                                    invoice?.paid,
                                ) { paymentDailog, articleDailog, pdfDailog, beforeSending ->
                                    showPaymentDailog = paymentDailog
                                    showArticleDialog = articleDailog
                                    showDialog = pdfDailog
                                    showPaymentDialog = beforeSending
                                }
                                if (showDialog) generatePDF(context, commandsLine)
                                if (showPaymentDialog) {
                                    val invoiceText = stringResource(id = R.string.invoice)
                                    ShowPaymentDailog(totgen, openDailog = true) { mony, payed ->
                                        paid = if (payed) {
                                            PaymentStatus.PAID
                                        } else {
                                            if (mony != BigDecimal.ZERO) {
                                                PaymentStatus.INCOMPLETE
                                            } else {
                                                PaymentStatus.NOT_PAID
                                            }
                                        }
                                        val invoicee = Invoice().copy()
                                        invoicee.rest = mony.toDouble()
                                        invoicee.paid = paid
                                        invoicee.code = invoiceViewModel.lastInvoiceCode
                                        invoicee.client = invoiceViewModel.clientCompany
                                        invoicee.person = invoiceViewModel.clientUser
                                        invoicee.provider = myCompany
                                        for (x in commandsLine) {
                                            x.invoice = invoicee
                                        }

                                        generatePDF(context, commandsLine)
                                        invoiceViewModel.addInvoice(invoiceMode)
                                        appViewModel.updateShow(invoiceText)
                                    }
                                }
                            }
                        }
                        ClientDetails(clientType, clientCompany, clientUser)
                        Column {
                            LazyRow {
                                item {
                                    Column {
                                        TableHeader(labels)
                                        LazyColumn(modifier = Modifier.heightIn(max = 600.dp)) {
                                            itemsIndexed(commandsLine) { index, commandLine ->
                                                Row(
                                                    modifier = Modifier
                                                        .padding(1.dp)
                                                        .width(800.dp)
                                                        .clickable {
                                                            articleIndex = index
                                                            showArticleDialog = true
                                                        }
                                                ) {
                                                    if (showArticleDialog && index == articleIndex) {
                                                        invoiceViewModel.article =
                                                            commandLine.article!!
                                                        invoiceViewModel.commandLineDto =
                                                            commandLine
                                                        ArticleDialog(
                                                            update = true,
                                                            openDialo = true
                                                        ) {
                                                            showArticleDialog = false
                                                        }
                                                    }
                                                    TableRow(commandLine)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            InvoiceFooter(
                                invoiceViewModel = invoiceViewModel,
                                tottva = tottva,
                                totprice = totprice,
                                totgen = totgen,
                                invoiceMode
                            )
                        }
                    }
                }
            }
        }

        InvoiceMode.UPDATE -> {
            if (invoice != null) {
                LazyColumn {
                    item {
                        MyCompanyDetails(myCompany = invoice?.provider!!)
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
                                    text = stringResource(
                                        id = R.string.invoice_date,
                                        invoice?.createdDate!!
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                                FeatureIcons(
                                    invoiceViewModel = invoiceViewModel,
                                    context = context,
                                    invoiceMode = invoiceMode,
                                    invoice?.paid,
                                ) { paymentDailog, articleDailog, pdfDailog, beforeSending ->
                                    showPaymentDailog = paymentDailog
                                    showArticleDialog = articleDailog
                                    showDialog = pdfDailog
                                    showPaymentDialog = beforeSending
                                }
                                if (showDialog) generatePDF(context, commandsLine)
                                if (showPaymentDailog) {
                                    PaymentDailog(isOpen = true) { amount ->
                                        showPaymentDailog = false
                                        if (amount != 0.0) {
                                            val cash = CashModel()
                                            cash.amount = amount
                                            cash.invoice = invoice
                                            paymentViewModel.sendRaglement(
                                                invoice?.provider?.id!!,
                                                cash
                                            ){
                                                invoice = it
                                            }
                                        }
                                    }
                                }
                                if (showPaymentDialog) {
                                    val invoiceText = stringResource(id = R.string.invoice)
                                    ShowPaymentDailog(
                                        totgen,
                                        openDailog = true
                                    ) { mony, payed ->
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
                                        if(invoiceViewModel.clientUser == User() && invoiceViewModel.clientCompany == Company()) {
                                            invoiceViewModel.clientUser = invoice?.person ?: User()
                                            invoiceViewModel.clientCompany = invoice?.client ?: Company()
                                        }
                                        invoiceViewModel.addInvoice(invoiceMode)
                                        appViewModel.updateShow(invoiceText)
                                    }
                                }
                            }
                        }
                        ClientDetails(
                            clientType = clientType,
                            clientCompany = invoice?.client,
                            clientUser = invoice?.person
                        )

                        if (commandsLine.isNotEmpty()) {
                            LazyRow {
                                item {
                                    Column {
                                        TableHeader(labels = labels)
                                        LazyColumn(
                                            modifier = Modifier.heightIn(max = 600.dp)
                                        ) {
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
                                                        invoiceViewModel.article =
                                                            commandLine.article!!
                                                        invoiceViewModel.commandLineDto =
                                                            commandLine
                                                        ArticleDialog(
                                                            update = true,
                                                            openDialo = true
                                                        ) {
                                                            showArticleDialog = false
                                                        }
                                                    }
                                                    TableRow(commandLine = commandLine)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            InvoiceFooter(invoiceViewModel, tottva, totprice, totgen, invoiceMode)
                        }
                        LazyColumn(modifier = Modifier.heightIn(max = 600.dp)) {
                            items(count = paymentHistoric.itemCount,
                                key = paymentHistoric.itemKey { it.id!! }
                            ) { index ->
                                val payment = paymentHistoric[index]
                                if (payment != null)
                                    PaymentCard(payment)

                            }
                        }
                    }
                }
            }
        }

        InvoiceMode.VERIFY -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    MyCompanyDetails(myCompany = invoice?.provider ?: Company())
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
                                text = stringResource(
                                    id = R.string.invoice_date,
                                    invoice?.createdDate ?: ""
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                            if (invoice?.status == Status.INWAITING) {
                                if (invoice?.provider?.id != myCompany.id) {
                                    ButtonSubmit(
                                        labelValue = stringResource(id = R.string.accept),
                                        color = Color.Green,
                                        enabled = true
                                    ) {
                                        invoiceViewModel.accepteInvoice(
                                            invoice?.id!!,
                                            Status.ACCEPTED
                                        )
                                    }
                                } else {
                                    Text(stringResource(id = R.string.waiting_for_accept))
                                }
                            }
                            if (showPaymentDailog) {
                                PaymentDailog(isOpen = true) { amount ->
                                    showPaymentDailog = false
                                    if (amount != 0.0) {
                                        val cash = CashModel()
                                        cash.amount = amount
                                        cash.invoice = invoice
                                        paymentViewModel.sendRaglement(
                                            invoice?.provider?.id!!,
                                            cash
                                        ){
                                            invoice = it
                                        }
                                    }
                                }
                            }
                            FeatureIcons(
                                invoiceViewModel = invoiceViewModel,
                                context = context,
                                invoiceMode,
                                invoice?.paid,
                            ) { paymentDailog, articleDailog, pdfDailog, beforeSending ->
                                showDialog = pdfDailog
                                showPaymentDailog = paymentDailog
                                showArticleDialog = beforeSending
                            }
                        }
                    }
                    ClientDetails(
                        clientType = clientType,
                        clientCompany = invoice?.client,
                        clientUser = invoice?.person
                    )
                    if (commandLineInvoice.itemCount != 0 || ordersLineInvoice.itemCount != 0) {
                        LazyRow {
                            item {
                                Column {
                                    TableHeader(labels = labels)
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 600.dp)
                                    ) {
                                        items(count = commandLineInvoice.itemCount,
                                            key = commandLineInvoice.itemKey { it.id!! }) { index ->
                                            val commandLine = commandLineInvoice[index]
                                            if (commandLine != null) {
                                                if(showDialog)
                                                    generatePDF(context , commandsLine)
                                                TableRow(commandLine = commandLine)
                                            }
                                        }
                                    }
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 600.dp)
                                    ) {
                                        items(count = ordersLineInvoice.itemCount,
                                            key = ordersLineInvoice.itemKey { it.id!! }) { index ->
                                            val order = ordersLineInvoice[index]
                                            if (order != null) {
                                                if(showDialog) {
                                                    val purchaseOrderList =
                                                        ordersLineInvoice.itemSnapshotList.items
                                                    generateOrderPDF(context, purchaseOrderList)
                                                }
                                                TableRowOrder(order)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    InvoiceFooter(
                        invoiceViewModel = invoiceViewModel,
                        tottva = invoice?.tot_tva_invoice?.toBigDecimal(),
                        totprice = invoice?.prix_article_tot?.toBigDecimal(),
                        totgen = invoice?.prix_invoice_tot?.toBigDecimal(),
                        invoiceMode
                    )
                }
                items(count = paymentHistoric.itemCount,
                    key = paymentHistoric.itemKey { it.id!! }
                ) { index ->
                    val payment = paymentHistoric[index]
                    if (payment != null)
                        PaymentCard(payment)

                }
            }
        }
    }
}

@Composable
fun InvoiceFooter(invoiceViewModel: InvoiceViewModel,tottva : BigDecimal?, totprice : BigDecimal?, totgen : BigDecimal?, invoiceType : InvoiceMode) {
    var discount by remember {
        mutableStateOf(if(invoiceType != InvoiceMode.CREATE) invoiceViewModel.discount.toString() else "")
    }
    val discountEnabled by remember {
        mutableStateOf(invoiceType != InvoiceMode.VERIFY)
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
                label = stringResource(id = R.string.discount),
                singleLine = true,
                maxLine = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                enabled = discountEnabled
            ) {
                if (it.matches(Regex("^[0-9]*[,.]?[0-9]*$"))) {
                    val normalizedInput = it.replace(',', '.')
                    discount = normalizedInput
                    invoiceViewModel.discount =
                        if (normalizedInput.startsWith(".") && normalizedInput.endsWith(
                                "."
                            )
                        ) {
                            0.0
                        } else if (normalizedInput.endsWith(".")) {
                            normalizedInput.let { inp ->
                                if (inp.toDouble() % 1.0 == 0.0) inp.toDouble() else 0.0
                            }
                        } else {
                            normalizedInput.toDoubleOrNull() ?: 0.0
                        }
                }
            }
            Text(text = stringResource(id = R.string.total_tva, tottva?:BigDecimal.ZERO))
            Text(text = stringResource(id = R.string.total_price, totprice?:BigDecimal.ZERO))
            Text(text = stringResource(id = R.string.total_general, totgen?:BigDecimal.ZERO))
        }
    }
}
@Composable
fun TableRow(commandLine: CommandLine) {
    val rowData = listOf(
        commandLine.article?.article?.libelle ?: "",
        commandLine.article?.article?.code ?: "",
        commandLine.quantity.toString(),
        commandLine.article?.unit.toString(),
        commandLine.article?.article?.tva.toString(),
        commandLine.article?.sellingPrice.toString(),
        commandLine.totTva.toString(),
        commandLine.prixArticleTot.toString(),
        commandLine.discount.toString()
    )

    Row(
        modifier = Modifier
            .padding(1.dp)
            .width(800.dp)
    ) {
        rowData.forEach { data ->
            Text(
                text = data,
                modifier = Modifier
                    .padding(end = 3.dp)
                    .weight(1f)
                    .background(color = Color.LightGray),
                textAlign = TextAlign.Start
            )
        }
    }
}
@Composable
fun TableRowOrder(commandLine: PurchaseOrderLine) {
    val rowData = listOf(
        commandLine.article?.article?.libelle ?: "",
        commandLine.article?.article?.code ?: "",
        commandLine.quantity.toString(),
        commandLine.article?.unit.toString(),
        commandLine.article?.article?.tva.toString(),
        commandLine.article?.sellingPrice.toString(),
        commandLine.totTva.toString(),
        commandLine.prixArticleTot.toString(),
        "0"
    )

    Row(
        modifier = Modifier
            .padding(1.dp)
            .width(800.dp)
    ) {
        rowData.forEach { data ->
            Text(
                text = data,
                modifier = Modifier
                    .padding(end = 3.dp)
                    .weight(1f)
                    .background(color = Color.LightGray),
                textAlign = TextAlign.Start
            )
        }
    }
}
@Composable
fun TableHeader(labels : List<Int>) {
    Row(
        modifier = Modifier
            .padding(1.dp)
            .width(800.dp)
    ) {
        labels.forEach { labelId ->
            Text(
                text = stringResource(id = labelId),
                modifier = Modifier
                    .padding(end = 3.dp)
                    .weight(1f)
                    .background(color = Color.LightGray),
                textAlign = TextAlign.Start
            )
        }
    }
}
@Composable
fun FeatureIcons(invoiceViewModel: InvoiceViewModel, context: Context, invoiceMode : InvoiceMode, invoiceStatus : PaymentStatus?, onSubmit: (Boolean, Boolean, Boolean, Boolean) -> Unit) {
    var article by remember {
        mutableStateOf(ArticleCompany())
    }
    var isShowQuantityDailog by remember {
        mutableStateOf(false)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
    ) {
        if(invoiceMode != InvoiceMode.VERIFY) {
        ArticleDialog(update = false, openDialo = false) {
            onSubmit(false, false, false, false)
        }
            IconButton(
                onClick = {
                    onSubmit(false, false, false, true)
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(id = R.string.send)
                )
            }
            val barcodenotfound = stringResource(id = R.string.barcode_notfound)
            IconButton(
                onClick = {
                    invoiceViewModel.startScan {
                        if (it == null) {
                            Toast.makeText(
                                context,
                                barcodenotfound,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            article = it
                            isShowQuantityDailog = true
                        }
                    }
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.AddToHomeScreen,
                    contentDescription = stringResource(id = R.string.Favorite)
                )
            }

            if (isShowQuantityDailog) {
                ShowQuantityDailog(article, true, invoiceViewModel, false) {
                    isShowQuantityDailog = false
                }
            }
        }
        IconButton(onClick = { onSubmit(false, false,true, false) }
        ) {
            Icon(
                Icons.AutoMirrored.Outlined.InsertDriveFile,
                contentDescription = stringResource(id = R.string.pdf)
            )
        }
        if(invoiceStatus != PaymentStatus.PAID && invoiceMode != InvoiceMode.CREATE)
        IconButton(onClick = { onSubmit(true, false, false, false) }) {
            Icon(imageVector = Icons.Outlined.AccountBalanceWallet,
                contentDescription = "payment icon")
        }

    }
}
@Composable
fun ClientDetails(clientType: AccountType, clientCompany : Company? , clientUser : User?) {
    var showClientDialog by remember {
        mutableStateOf(false)
    }
    Row {
        Column(
            modifier = Modifier.clickable {
                showClientDialog = true
            }
        ) {
            if (showClientDialog) {
                ClientDialog(update = true, openDialoge = true) {
                    showClientDialog = false
                }
            }
            Text(text = if (clientType == AccountType.COMPANY) clientCompany?.name?:"" else clientUser?.username?:"")
            Text(
                text = if (clientType == AccountType.COMPANY) clientCompany?.phone
                    ?: "" else clientUser?.phone ?: ""
            )
            Text(
                text = if (clientType == AccountType.COMPANY) clientCompany?.address
                    ?: "" else clientUser?.address ?: ""
            )
        }
    }
}
@Composable
fun MyCompanyDetails(myCompany : Company) {
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
                    image = "${BASE_URL}${stringResource(id = R.string.warehouse)}/${
                        stringResource(
                            id = R.string.image
                        )
                    }/${myCompany.logo}/${stringResource(id = R.string.company)}/${myCompany.user?.id}"
                )
                Text(text = myCompany.email ?: "")
                myCompany.matfisc?.let { Text(text = it) }
            }
        }
    }
}
@Composable
fun PaymentCard(payment : Payment) {
    Card(
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column {

            Row {
                Text(text = "name : ${payment.invoice?.client?.name ?: payment.invoice?.person?.username!!}")
                Text(text = "invoice code : ${payment.invoice?.code}")
            }
            Row {

                Text(text = "amount : ${payment.amount}")
                Text(text = "payment status : ${payment.status}")
            }
            Row {
                Text(text = "payment date : ${payment.lastModifiedDate}")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun stringToLocalDateTime(dateTimeString: String, format: String = "yyyy-MM-dd"): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern(format)
    return LocalDateTime.parse(dateTimeString, formatter)
}
@Composable
fun PaymentDailog(isOpen : Boolean, onSubmit : (Double) -> Unit) {
    val openDailog by remember {
        mutableStateOf(isOpen)
    }
    var amountText by remember {
        mutableStateOf("")
    }
    var amountDouble by remember {
        mutableDoubleStateOf(0.0)
    }
    if(openDailog){
        Dialog(
            onDismissRequest = {
                onSubmit(amountDouble)
            }
        ) {
            Column {
                InputTextField(
                    labelValue = amountText,
                    label = "amount",
                    singleLine = true,
                    maxLine = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    onValueChange = {
                        if (it.matches(Regex("^[0-9]*[,.]?[0-9]*$"))) {
                            val normalizedInput = it.replace(',', '.')
                            amountText = normalizedInput
                            amountDouble = if (normalizedInput.startsWith(".") && normalizedInput.endsWith(".")) {
                                0.0
                            }else if (normalizedInput.endsWith(".")) {
                                normalizedInput.let { inp ->
                                    if (inp.toDouble() % 1.0 == 0.0) inp.toDouble() else 0.0
                                }
                            } else {
                                normalizedInput.toDoubleOrNull() ?: 0.0
                            }
                        }
                    },
                    onImage = {}
                ) {

                }
                Row {
                    Row (modifier = Modifier.weight(1f))
                    {
                        ButtonSubmit(labelValue = stringResource(id = R.string.submit), color = Color.Green, enabled = true) {
                            onSubmit(amountDouble)
                        }
                    }
                    Row (modifier = Modifier.weight(1f))
                    {
                     ButtonSubmit(labelValue = stringResource(id = R.string.cancel), color = Color.Red, enabled = true) {
                         onSubmit(amountDouble)
                     }
                    }
                }
            }
        }
    }




}