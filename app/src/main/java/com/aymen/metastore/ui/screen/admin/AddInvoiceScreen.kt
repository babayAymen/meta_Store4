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
import androidx.compose.material3.TextField
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
import androidx.compose.ui.res.painterResource
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
import com.aymen.metastore.ui.component.NotImage
import com.aymen.metastore.ui.component.ShowImage
import com.aymen.metastore.ui.component.ShowPaymentDailog
import com.aymen.metastore.ui.component.ShowQuantityDailog
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
fun AddInvoiceScreen() {
    val context = LocalContext.current
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val invoiceViewModel: InvoiceViewModel = hiltViewModel()
    val paymentViewModel: PaymentViewModel = hiltViewModel()
    val appViewModel: AppViewModel = hiltViewModel()
    val myCompany by sharedViewModel.company.collectAsStateWithLifecycle()
    val myUser by sharedViewModel.user.collectAsStateWithLifecycle()
    val myAccountType by sharedViewModel.accountType.collectAsStateWithLifecycle()
    val asProvider = invoiceViewModel.asProvider
    val clientCompany = invoiceViewModel.clientCompany
    val clientUser = invoiceViewModel.clientUser
    val clientType = invoiceViewModel.clientType
    val currentDateTime = LocalDateTime.now()
    val formattedDate = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    var showDialog by remember { mutableStateOf(false) }
    val invoiceMode by invoiceViewModel.invoiceModeState.collectAsStateWithLifecycle()
    val provider by invoiceViewModel.providerCompany.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = Unit) {
        invoiceViewModel.remiseCommandLineToZero()
        when (invoiceMode) {
                    InvoiceMode.CREATE ->  invoiceViewModel.getLastInvoiceCode()
            else -> {
                invoiceViewModel.getInvoiceDetails()
                if(invoiceViewModel.invoice.type == InvoiceDetailsType.COMMAND_LINE)
                paymentViewModel.getPaymentHystoricByInvoiceId(invoiceId = invoiceViewModel.invoice.id?:0)
            }
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
            ordersLineInvoice.itemSnapshotList.items.firstOrNull()?.invoice?:invoiceViewModel.invoice
        else
            commandLineInvoice.itemSnapshotList.items.firstOrNull()?.invoice

    var isMe by remember {
        mutableStateOf(false)
    }
    DisposableEffect(key1 = Unit) {
        onDispose {
            invoiceViewModel.remiseOrderLineToZero()
            paymentViewModel.setPaymentHistoric()
            if(invoice?.status == Status.REFUSED && isMe)
                invoiceViewModel.deleteInvoiceByIdLocally(invoice?.id!!)
        }
    }
    LaunchedEffect(key1 = ordersLineInvoice.itemCount, key2 = commandLineInvoice.itemCount) {
        if(invoice?.provider?.id == myCompany.id)
            isMe = true
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
            invoiceViewModel.calculateInvoiceDetails(){tva , article , general ->
                tottva = tva
                totprice = article
                totgen = general
            }
            totprice = totprice.setScale(2, RoundingMode.HALF_UP)
            tottva = tottva.setScale(2, RoundingMode.HALF_UP)
            totgen = totgen.setScale(2, RoundingMode.HALF_UP)

        }
    }
    var articleIndex by remember {
        mutableIntStateOf(-1)
    }
    var paid by remember {
        mutableStateOf(PaymentStatus.NOT_PAID)
    }
    var showButton by remember {
        mutableStateOf(true)
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        LazyColumn (
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                MyCompanyDetails(if (invoiceMode == InvoiceMode.CREATE) provider else invoice?.provider ?: Company())
                Column {
                    Text(
                        text = if (invoiceMode == InvoiceMode.CREATE) invoiceViewModel.lastInvoiceCode.toString() else invoice?.code.toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = stringResource(
                            id = R.string.invoice_date,
                            if (invoiceMode == InvoiceMode.CREATE) formattedDate else stringToLocalDateTime( invoice?.createdDate)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )

                    if (invoice?.status != Status.REFUSED)
                        FeatureIcons(
                            invoiceViewModel, context, invoiceMode,
                            invoice?.paid, isMe, myAccountType = myAccountType, asProvider = asProvider, provider.id?:0
                        ) { paymentDailog, articleDailog, pdfDailog, beforeSending ->
                            showPaymentDailog = paymentDailog
                            showArticleDialog = articleDailog
                            showDialog = pdfDailog
                            showPaymentDialog = beforeSending
                        }
                    else {
                        Text(text = invoice?.status.toString())
                        Toast.makeText(
                            context,
                            "this invoice is deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                        invoiceViewModel.deleteInvoiceById(invoice?.id!!)
                    }
                    if (invoice?.status == Status.INWAITING && (invoice?.client?.id == myCompany.id || invoice?.person?.id == myUser.id) && showButton) {
                        Row {
                            Row(modifier = Modifier.weight(1f)) {
                                ButtonSubmit(
                                    labelValue = stringResource(id = R.string.refuse),
                                    color = Color.Red,
                                    enabled = true
                                ) {
                                    showButton = false
                                    invoiceViewModel.accepteInvoice(
                                        invoice?.id!!,
                                        Status.REFUSED
                                    )
                                }
                            }
                            Row(modifier = Modifier.weight(1f)) {
                                ButtonSubmit(
                                    labelValue = stringResource(id = R.string.accept),
                                    color = Color.Green,
                                    enabled = true
                                ) {
                                    showButton = false
                                    invoiceViewModel.accepteInvoice(
                                        invoice?.id!!,
                                        Status.ACCEPTED
                                    )
                                }
                            }
                        }
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
                                ) {
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
                            invoicee.client = if(invoiceViewModel.clientCompany.id != null)invoiceViewModel.clientCompany else null
                            invoicee.person = if(invoiceViewModel.clientUser.id != null)invoiceViewModel.clientUser else null
                            Log.e("invoiceperson","person : ${invoicee.person}")
                            invoicee.provider = provider
                            invoicee.discount = invoiceViewModel.discount
                            for (x in commandsLine) {
                                x.invoice = invoicee
                            }

                            generatePDF(context, commandsLine)
                            invoiceViewModel.addInvoice(invoiceMode, asProvider, invoicee)
                            appViewModel.updateShow(invoiceText)
                        }
                    }

                }
                ClientDetails(
                    clientType,
                    if (invoiceMode == InvoiceMode.CREATE) clientCompany else invoice?.client,
                    if (invoiceMode == InvoiceMode.CREATE) clientUser else invoice?.person
                )

                LazyRow {
                    item {
                        Column {
                            TableHeader()
                            LazyColumn(modifier = Modifier.heightIn(max = 600.dp)) {

                                itemsIndexed(commandsLine) { index, commandLine ->
                                    Column(
                                        modifier = Modifier
                                            .padding(1.dp)
                                            .width(800.dp)
                                            .clickable {
                                                articleIndex = index
                                                showArticleDialog = true
                                            }
                                    ) {
                                        if (showArticleDialog && index == articleIndex && invoiceMode != InvoiceMode.VERIFY) {
                                            invoiceViewModel.article = commandLine.article!!
                                            // il faux ajouter quantity
                                            invoiceViewModel.commandLineDto = commandLine
                                            ArticleDialog(
                                                update = true,
                                                openDialo = true,
                                                asProvider,
                                                provider.id!!,
                                                isSubArticle = false
                                            ) {art , bool ->
                                                showArticleDialog = false
                                            }
                                        }
                                        TableRow(commandLine = commandLine)
                                    }
                                }
                                if (invoiceMode == InvoiceMode.VERIFY) {
                                    when(invoice?.type){
                                        InvoiceDetailsType.COMMAND_LINE -> items(count = commandLineInvoice.itemCount,
                                            key = commandLineInvoice.itemKey { it.id!! }) { index ->
                                            val commandLine = commandLineInvoice[index]
                                            if (commandLine != null) {
                                                TableRow(commandLine = commandLine)
                                            }
                                        }
                                        InvoiceDetailsType.ORDER_LINE ->  items(count = ordersLineInvoice.itemCount,
                                            key = ordersLineInvoice.itemKey { it.id!! }) { index ->
                                            val order = ordersLineInvoice[index]
                                            if (order != null) {
                                                if (showDialog) {
                                                    val purchaseOrderList =
                                                        ordersLineInvoice.itemSnapshotList.items
                                                    generateOrderPDF(
                                                        context,
                                                        purchaseOrderList
                                                    )
                                                }
                                                TableRowOrder(order)
                                            }
                                        }
                                        null -> {}
                                    }


                                }
                            }
                        }
                    }
                }
                if (invoiceMode == InvoiceMode.VERIFY) {
                    tottva = invoice?.tot_tva_invoice?.toBigDecimal()
                    totprice = invoice?.prix_article_tot?.toBigDecimal()
                    totgen = invoice?.prix_invoice_tot?.toBigDecimal()
                }
                InvoiceFooter(
                    invoiceViewModel = invoiceViewModel,
                    tottva = tottva,
                    totprice = totprice,
                    totgen = totgen,
                    invoiceMode
                )
            }
            if (paymentHistoric.itemCount != 0) {
                items(count = paymentHistoric.itemCount,
                    key = paymentHistoric.itemKey { it.id!! }
                ) { index ->
                    val payment = paymentHistoric[index]
                    if (payment != null && payment.invoice?.id == invoice?.id)
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
fun TableHeader() {
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
fun FeatureIcons(invoiceViewModel: InvoiceViewModel, context: Context, invoiceMode : InvoiceMode, invoiceStatus : PaymentStatus?, isMe : Boolean,
                myAccountType: AccountType, asProvider  : Boolean, providerId : Long ,onSubmit: (Boolean, Boolean, Boolean, Boolean) -> Unit) {
    var openDeliveryDialog by remember {
        mutableStateOf(false)
    }
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
        if (invoiceMode != InvoiceMode.VERIFY) {
            ArticleDialog(update = false, openDialo = false, asProvider,providerId, false) {art , bool ->
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
        IconButton(onClick = { onSubmit(false, false, true, false) }
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_picture_as_pdf_24),
                contentDescription = stringResource(id = R.string.pdf)
            )
        }
        if (invoiceStatus != PaymentStatus.PAID && invoiceMode != InvoiceMode.CREATE && isMe) {
            IconButton(onClick = { onSubmit(true, false, false, false) }) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = "payment icon"
                )
            }
        }
        if (myAccountType == AccountType.DELIVERY) {
            if (invoiceViewModel.purchaseOrder.isTaken == false)
                ButtonSubmit(
                    labelValue = stringResource(id = R.string.accept),
                    color = Color.Green,
                    enabled = true
                ) {
                    invoiceViewModel.acceptInvoiceAsDelivery()
                }
            else {
                if(invoiceViewModel.purchaseOrder.isDelivered == false) {
                    Row {
                        Row(
                            modifier = Modifier.weight(1f)
                        ) {
                            ButtonSubmit(
                                labelValue = stringResource(id = R.string.delivery_order),
                                color = Color.Green,
                                enabled = true
                            ) {
                                openDeliveryDialog = true
                            }

                        }
                        Row(
                            modifier = Modifier.weight(1f)
                        ) {

                            ButtonSubmit(
                                labelValue = stringResource(id = R.string.reject),
                                color = Color.Red,
                                enabled = true
                            ) {
                                openDeliveryDialog = true
                            }
                        }
                        if (openDeliveryDialog)
                            DeliveryCodeDailog(isOpen = openDeliveryDialog) { closeDialog, deliverycode ->
                                openDeliveryDialog = closeDialog
                                if (deliverycode != "")
                                    invoiceViewModel.submitOrderDelivered(deliverycode)
                            }
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryCodeDailog(isOpen: Boolean, onSubmit: (Boolean, String) -> Unit) {
   if(isOpen) {
       var text by remember {
           mutableStateOf("")
       }
       var isEnabled by remember {
           mutableStateOf(false)
       }
       Dialog(onDismissRequest = { onSubmit(false,"") }) {
           Column {
               InputTextField(
                   labelValue = text,
                   label = "delivery code",
                   singleLine = true,
                   maxLine = 1,
                   keyboardOptions = KeyboardOptions(),
                   onValueChange = { text = it
                       isEnabled = text.length == 6
                                   },
                   onImage = {}
               ) {

               }
               Row {
                   Row(
                       modifier = Modifier.weight(1f)
                   ) {
                       ButtonSubmit(
                           labelValue = stringResource(id = R.string.accept),
                           color = Color.Green,
                           enabled = isEnabled
                       ) {
                           onSubmit(false,text)
                       }
                   }
                   Row(
                       modifier = Modifier.weight(1f)
                   ) {
                       ButtonSubmit(
                           labelValue = stringResource(id = R.string.cancel),
                           color = Color.Red,
                           enabled = true
                       ) {
                           onSubmit(false,"")
                       }
                   }
               }
           }
       }
   }
}
@Composable
fun ClientDetails(clientType: AccountType, clientCompany : Company? , clientUser : User?) {
    var showClientDialog by remember {
        mutableStateOf(false)
    }
    Log.e("clienttest","client company : $clientCompany user : $clientUser type $clientType")
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
            Text(text = if (clientType == AccountType.COMPANY) clientCompany?.phone ?: "" else clientUser?.phone ?: "")
            Text(text = if (clientType == AccountType.COMPANY) clientCompany?.address ?: "" else clientUser?.address ?: "")
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
                if(myCompany.logo != null)
                     ShowImage(image = String.format(IMAGE_URL_COMPANY,myCompany.logo,myCompany.user?.id))
                else
                    NotImage()
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
fun stringToLocalDateTime(dateTimeString: String?): String {
    if(dateTimeString != null) {
        val dateTime = LocalDateTime.parse(dateTimeString)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return dateTime.format(dateFormatter)
    }else
        return ""
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