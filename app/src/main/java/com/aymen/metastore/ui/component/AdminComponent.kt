package com.aymen.store.ui.component

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.metastore.R
import com.aymen.metastore.model.Enum.InvoiceDetailsType
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.entity.Dto.ArticleCompanyDto
import com.aymen.metastore.model.entity.converterRealmToApi.mapRoomCategoryToCategory
import com.aymen.metastore.model.entity.room.Article
import com.aymen.metastore.model.entity.room.Category
import com.aymen.metastore.model.entity.room.ClientProviderRelation
import com.aymen.metastore.model.entity.room.Company
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyClient
import com.aymen.metastore.model.entity.roomRelation.InventoryWithArticle
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.store.dependencyInjection.BASE_URL
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.PrivacySetting
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.Enum.UnitArticle
import com.aymen.store.model.entity.dto.CommandLineDto
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.CategoryViewModel
import com.aymen.store.model.repository.ViewModel.CompanyViewModel
import com.aymen.store.model.repository.ViewModel.InvoiceViewModel
import com.aymen.store.model.repository.ViewModel.PaymentViewModel
import com.aymen.store.model.repository.ViewModel.SubCategoryViewModel
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun Item(label : String) {
    val viewModel : AppViewModel = viewModel()
    Card(
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .padding(8.dp)
            .size(100.dp, 100.dp),
        onClick = {viewModel.updateShow(label)}
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Text(
                text = label,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}


@Composable
fun RadioButtons(onPrivacySelected: (PrivacySetting) -> Unit) {
    val privacyOptions = listOf(
        PrivacySetting.PUBLIC,
        PrivacySetting.CLIENT,
        PrivacySetting.ONLY_ME
    )

    val selectedPrivacyIndex = remember { mutableStateOf(0) }

    Row {
        privacyOptions.forEachIndexed { index, privacy ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    selectedPrivacyIndex.value = index
                    onPrivacySelected(privacy)
                }
            ) {
                RadioButton(
                    selected = index == selectedPrivacyIndex.value,
                    onClick = {
                        selectedPrivacyIndex.value = index
                        onPrivacySelected(privacy)
                    }
                )
                Text(text = privacy.toString())
            }
        }
    }
}

data class ToggleleableInfo(val isChecked: Boolean, val text: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dropDownItems(list: List<UnitArticle>):UnitArticle {
    var itemSelected by remember {
        mutableStateOf(list[0])
    }
    var isExpanded by remember {
        mutableStateOf(false)
    }
    Box(modifier = Modifier.wrapContentHeight()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = { isExpanded = !isExpanded }
            ) {
                TextField(
                    modifier = Modifier.menuAnchor(),
                    value = itemSelected.toString(),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
                )

                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }) {
                    list.forEachIndexed { index, text ->
                        DropdownMenuItem(
                            text = { Text(text.toString()) },
                            onClick = {
                                itemSelected = list[index]
                                isExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
    }
    return  itemSelected
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownCategory(list: List<Category>) {
    if (list.isNotEmpty()) {
        val categoryViewModel : CategoryViewModel = viewModel()
        var itemSelected by remember {
            mutableStateOf(list[0])
        }
        categoryViewModel.category = mapRoomCategoryToCategory(itemSelected)
        var isExpanded by remember {
            mutableStateOf(false)
        }
        Box(modifier = Modifier.wrapContentHeight()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = !isExpanded }
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor(),
                        value = itemSelected.libelle!!,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
                    )

                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false }) {
                        list.forEachIndexed { index, text ->
                            DropdownMenuItem(
                                text = { Text(text.libelle!!) },
                                onClick = {
                                    itemSelected = list[index]
                                    isExpanded = false
                                    categoryViewModel.category = mapRoomCategoryToCategory(itemSelected)
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownSubCategory(list : List<com.aymen.metastore.model.entity.room.SubCategory>, categoryId : Long, onSelected : (com.aymen.metastore.model.entity.room.SubCategory) -> Unit) {
    val subCategoryViewModel : SubCategoryViewModel = hiltViewModel()
    var itemSelected by remember {
        mutableStateOf("select sub category")
    }
        val subCategoryList by subCategoryViewModel.subCategories.collectAsStateWithLifecycle()
    LaunchedEffect(categoryId) {
         if(subCategoryList.isNotEmpty()){
             itemSelected = subCategoryList[0].libelle!!
            onSelected(subCategoryList[0])
        }else{
            "select sub category"
        }
    }
    var isExpanded by remember {
        mutableStateOf(false)
    }
    Box(modifier = Modifier.wrapContentHeight()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = { isExpanded = !isExpanded }
            ) {
                TextField(
                    modifier = Modifier.menuAnchor(),
                    value = itemSelected,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
                )

                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }) {
                    list.forEach { subCategory ->
                        DropdownMenuItem(
                            text = { Text(subCategory.libelle!!) },
                            onClick = {
                                itemSelected = subCategory.libelle!!
                                isExpanded = false
                                subCategoryViewModel.subCategoryId = subCategory.id!!
                                onSelected(subCategory)
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownCompany(list: List<CompanyWithCompanyClient>) {
    val companyViewModel : CompanyViewModel = hiltViewModel()
    if (list.isNotEmpty()) {
        var itemSelected by remember {
            mutableStateOf(list[0])
        }
        var isExpanded by remember {
            mutableStateOf(false)
        }
        Box(modifier = Modifier.wrapContentHeight()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = !isExpanded }
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor(),
                        value = itemSelected.provider.name,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
                    )

                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false }) {
                        list.forEachIndexed { index, text ->
                            DropdownMenuItem(
                                text = { Text(text.provider.name) },
                                onClick = {
                                    itemSelected = list[index]
                                    isExpanded = false
                                    companyViewModel.providerId = itemSelected.provider.id!!
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownCompanyCategory(onSelected: (CompanyCategory) -> Unit) {
        var itemSelected by remember {
            mutableStateOf(CompanyCategory.DAIRY)
        }
        var isExpanded by remember {
            mutableStateOf(false)
        }
        Box(modifier = Modifier.wrapContentHeight()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = !isExpanded }
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor(),
                        value = itemSelected.toString(),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
                    )

                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false }) {
                        CompanyCategory.entries.forEach { text ->
                            DropdownMenuItem(
                                text = { Text(text.toString()) },
                                onClick = {
                                    itemSelected = text
                                    isExpanded = false
                                    onSelected(text)
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }
        }

}


@Composable
fun ArticleCardForAdmin(articleCompany : com.aymen.metastore.model.entity.room.ArticleCompany,articlee: com.aymen.metastore.model.entity.room.Article, image : String, onSelected: () -> Unit) {
    val articlecompany = articleCompany
    val article = articlee
    Row {
        Card(
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier
                .padding(4.dp)
                .clickable {
                    onSelected()
                }
        ) {
            Row {
                Column (
                    modifier = Modifier.weight(0.7f)
                ){
                    NormalText(value = article.libelle, aligne = TextAlign.Start)
                    article.code?.let { NormalText(value = it, aligne = TextAlign.Start) }
                    NormalText(value = articlecompany.unit.toString(), aligne = TextAlign.Start)
                    NormalText(value = article.tva.toString(), aligne = TextAlign.Start)
                    NormalText(value = articlecompany.sellingPrice.toString(), aligne = TextAlign.Start)
                    NormalText(value = articlecompany.minQuantity.toString(), aligne = TextAlign.Start)
                    article.barcode?.let { NormalText(value = it, aligne = TextAlign.Start) }
                    article.discription?.let { NormalText(value = it, aligne = TextAlign.Start) }
                    article.discription?.let { NormalText(value = it, aligne = TextAlign.Start) }

                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        article.tva?.let { ShowPrice(cost = articlecompany.cost?:0.0, margin = articlecompany.sellingPrice!!, tva = it) }
                        ArticleDetails(value = articlecompany.quantity.toString(), aligne = TextAlign.Start)
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(0.3f)
                        .align(Alignment.CenterVertically)
                ) {
                    ShowImage(image = image)
                }

            }
        }

    }
}

@Composable
fun addQuantityDailog(article: com.aymen.metastore.model.entity.room.ArticleCompany,articlee : com.aymen.metastore.model.entity.room.Article,company : Company, openDailoge: Boolean, onSubmit: (Double) -> Unit) {
    var openDialog by remember { mutableStateOf(openDailoge) }
    var quantity by remember {
        mutableStateOf(0.0)
    }
    if (openDialog) {
        Dialog(
            onDismissRequest = {
                openDialog = false
            }
        ) {
            Surface(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp)),
            ) {
                Column {
                    NormalText(value = "Add com.aymen.metastore.model.entity.room.Article Quantiry", aligne = TextAlign.Center)
                    Spacer(modifier = Modifier.padding(10.dp))
                    Row {
                        Row(
                            modifier = Modifier.weight(1f)
                        ) {
                            NormalText(value = articlee.libelle, aligne = TextAlign.Start)
                        }
                        Row (
                            modifier = Modifier.weight(1f)
                        ){
                            ShowImage(
                                image = "${BASE_URL}werehouse/image/${articlee.image}/article/${
                                    company.category?.ordinal
                                }"
                            )
                        }
                    }
                    InputTextField(
                        labelValue = if(quantity == 0.0)"" else quantity.toString(),
                        label = "Quantity",
                        singleLine = true,
                        maxLine = 1,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Go,
                            autoCorrect = true
                        ),
                        onValueChange = {
                            quantity = it.toDouble()
                        },
                        onImage = {}
                    ) {

                    }
                    Row {
                        Row(
                            modifier = Modifier.weight(1f)
                        ) {
                            ButtonSubmit(labelValue = "Submit", color = Color.Green, enabled = true) {
                                onSubmit(quantity)
                            }
                        }
                        Row(
                            modifier = Modifier.weight(1f)
                        ) {
                           ButtonSubmit(labelValue = "Cancel", color = Color.Red, enabled = true) {
                               openDialog = false
                           }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun CategoryCardForAdmin(category: Category, image : String) {
    Row(
        modifier = Modifier.padding(5.dp)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier
                .padding(4.dp)
        ) {
            Row {
                Column (
                    modifier = Modifier.weight(0.7f)
                ){
                    NormalText(value = category.libelle!!, aligne = TextAlign.Start)
                    NormalText(value = category.code?:"without code", aligne = TextAlign.Start)

                }
                Column(
                    modifier = Modifier
                        .weight(0.3f)
                        .align(Alignment.CenterVertically)
                ) {
                    if(category.image != null){
                    ShowImage(image = image)
                    }else{
                        Image(painter = painterResource(id = R.drawable.empty), contentDescription = "category")
                    }
                }

            }
        }

    }
}

@Composable
fun SubCategoryCardForAdmin(subCategory: com.aymen.metastore.model.entity.room.SubCategory, image : String,
                            category: Category) {
   val cat by remember {
       mutableStateOf(category)
   }
    Row(
        modifier = Modifier.padding(5.dp)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier
                .padding(4.dp)
        ) {
            Row {
                Column (
                    modifier = Modifier.weight(0.7f)
                ){
                    NormalText(value = subCategory.libelle!!, aligne = TextAlign.Start)
                    NormalText(value = subCategory.code?:"without code", aligne = TextAlign.Start)
                     NormalText(value = cat.libelle?:"", aligne = TextAlign.Start)

                }
                Column(
                    modifier = Modifier
                        .weight(0.3f)
                        .align(Alignment.CenterVertically)
                ) {
                    if(subCategory.image != null){
                    ShowImage(image = image)
                    }else{
                        Image(painter = painterResource(id = R.drawable.empty), contentDescription = "category")
                    }
                }

            }
        }

    }
}

@Composable
fun InventoryCard(inventory: InventoryWithArticle, image : String) {
    Row(
        modifier = Modifier.padding(5.dp)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier
                .padding(4.dp)
        ) {
            Row {
                Column (
                    modifier = Modifier.weight(0.7f)
                ){
                    NormalText(value = inventory.article.article.libelle, aligne = TextAlign.Start)
                    inventory.article.article.code?.let { NormalText(value = it, aligne = TextAlign.Start) }
                    NormalText(value = inventory.article.articleCompany.quantity.toString(), aligne = TextAlign.Start)

                }
                Column(
                    modifier = Modifier
                        .weight(0.3f)
                        .align(Alignment.CenterVertically)
                ) {
                    ShowImage(image = image)
                }

            }
        }

    }
}

@Composable
fun ArticleCard(modifier: Modifier = Modifier, article: Article, onSelected: () -> Unit) {
    Row(
        modifier = Modifier.padding(5.dp)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier
                .padding(4.dp)
        ) {
            NormalText(value = article.libelle, aligne = TextAlign.Start)
            NormalText(value = article.tva.toString(), aligne = TextAlign.Start)
            article.discription?.let { NormalText(value = it, aligne = TextAlign.Start) }
            ButtonSubmit(labelValue = "add", color = Color.Green, enabled = true) {
                onSelected()
            }
        }
    }
}

@Composable
fun ClientCard(client: CompanyWithCompanyClient, image : String) {
    Row(
        modifier = Modifier.padding(5.dp)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier
                .padding(4.dp)
        ) {
            Row {
                Column (
                    modifier = Modifier.weight(0.7f)
                ){
                       client.clientCompany?.let { NormalText(value = it.name, aligne = TextAlign.Start) }
                       client.clientCompany?.let { it.code?.let { it1 -> NormalText(value = it1, aligne = TextAlign.Start) } }
                    client.clientUser?.username?.let { NormalText(value = it, aligne = TextAlign.Start) }
                    NormalText(value = client.relation.mvt.toString(), aligne = TextAlign.Start)

                }
                Column(
                    modifier = Modifier
                        .weight(0.3f)
                        .align(Alignment.CenterVertically)
                ) {
                        Log.e("showImageclinetcard",image)
                    if(client.clientCompany?.logo != null || client.clientUser?.image != null){
                    ShowImage(image = image)
                    }else {
                        val painter: Painter = painterResource(id = R.drawable.emptyprofile)
                        Image(
                            painter = painter,
                            contentDescription = "empty photo profil",
                            modifier = Modifier
                                .size(30.dp)
                                .clip(
                                    RoundedCornerShape(10.dp)
                                )
                        )
                    }
                }

            }
        }

    }
}

@Composable
fun ProviderCard(provider: CompanyWithCompanyClient, image : String) {
    Row(
        modifier = Modifier.padding(5.dp)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier
                .padding(4.dp)
        ) {
            Row {
                Column (
                    modifier = Modifier.weight(0.7f)
                ){
                       provider.provider?.let { NormalText(value = it.name, aligne = TextAlign.Start) }
                    //   client.client?.let { NormalText(value = it.code, aligne = TextAlign.Start) }
                    NormalText(value = provider.relation.mvt.toString(), aligne = TextAlign.Start)

                }
                Column(
                    modifier = Modifier
                        .weight(0.3f)
                        .align(Alignment.CenterVertically)
                ) {
                    ShowImage(image = image)
                }

            }
        }

    }
}


@Composable
fun ParentCard(parent: Company) {
    Row(
        modifier = Modifier.padding(5.dp)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier
                .padding(4.dp)
        ) {
            Row {
                Column (
                    modifier = Modifier.weight(0.7f)
                ){
                    NormalText(value = parent.name, aligne = TextAlign.Start)
                    parent.code?.let { NormalText(value = it, aligne = TextAlign.Start) }

                }
            }
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InvoiceCard(invoice: InvoiceWithClientPersonProvider, appViewModel: AppViewModel, invoiceViewModel: InvoiceViewModel, asProvider : Boolean) {
val context = LocalContext.current
    Column(
        modifier = Modifier.padding(5.dp)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier
                .padding(4.dp)
                .clickable {
                    invoiceViewModel.invoice = invoice
                    invoiceViewModel.discount = invoice.invoice.discount
                    invoiceViewModel.invoiceMode = InvoiceMode.UPDATE
                    if (invoice.invoice.type == InvoiceDetailsType.ORDER_LINE || invoice.invoice.status == Status.ACCEPTED || !asProvider) {
                        invoiceViewModel.invoiceMode = InvoiceMode.VERIFY
                    }
                    appViewModel.updateShow("add invoice")
                }
        ) {
            Row {
                Column (
                    modifier = Modifier.weight(0.7f)
                ){
                    NormalText(value = invoice.invoice.code.toString(), aligne = TextAlign.Center)
                    if(asProvider) {
                        invoice.client?.let {
                        NormalText(
                            value = it.name,
                            aligne = TextAlign.Start
                        )
                        }
                        invoice.person?.let {
                            NormalText(
                                value = it.username!!,
                                aligne = TextAlign.Start
                            )
                        }
                    }else {
                        NormalText(
                            value = invoice.provider.name,
                            aligne = TextAlign.Start
                        )
                    }
                    NormalText(value = invoice.invoice.prix_invoice_tot.toString(), aligne = TextAlign.End)

                }
            }
        }

    }
}

@Composable
fun TableRowContent(item: CommandLineDto) {
    Row(modifier = Modifier.fillMaxWidth()) {
        TableColumnItem(item.article?.article?.libelle!!)
        TableColumnItem(item.article?.article?.code?:"")
        TableColumnItem(item.quantity.toString())
        TableColumnItem(item.article?.unit.toString())
        TableColumnItem(item.article?.article?.tva.toString())
        TableColumnItem(item.article?.cost.toString())
    }
}
@Composable
fun TableColumnItem(text: String) {
    Column(
        modifier = Modifier
            //weight(1f)
            .padding(horizontal = 4.dp)
    ) {
        Text(text)
    }
}

@Composable
fun ClientDialog(update : Boolean ,openDialoge : Boolean, onSubmit : () -> Unit) {
    var openDialog by remember {
        mutableStateOf(openDialoge)
    }
    var clientExist by remember {
        mutableStateOf(false)
    }
    ButtonSubmit(labelValue = "Add invoice", color = Color.Green, enabled = true) {
        openDialog = true
    }
    if(openDialog){
        Dialog(
            onDismissRequest = {
                openDialog = false
            }
        ){
            Surface(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp)),
            ) {
                Column {
                    Row {
                AutoCompleteClient(update = update){
                    clientExist = it
                }
                    }
                    Row {
                        Row (
                            modifier = Modifier.weight(1f)
                        ){
                        ButtonSubmit(labelValue = "ok", color = Color.Green, enabled = clientExist) {
                            onSubmit()
                            openDialog = false
                        }
                        }
                        Row (
                            modifier = Modifier.weight(1f)
                        ){

                        ButtonSubmit(labelValue = "cancel", color = Color.Red, enabled = true) {
                           openDialog = false
                        }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleDialog(update : Boolean ,openDialo : Boolean, onSubmit: () -> Unit) {
    val invoiceViewModel : InvoiceViewModel = viewModel()
    var openDialog by remember {
        mutableStateOf(openDialo)
    }
    var quantity by remember {
        mutableDoubleStateOf(0.0)
    }
    var discount by remember {
        mutableDoubleStateOf(0.0)
    }
    var articleExist by remember {
        mutableStateOf(false)
    }
    if(update){
        articleExist = true
        quantity = invoiceViewModel.commandLineDto.quantity
        discount = invoiceViewModel.commandLineDto.discount?:0.0
    }
    IconButton(onClick = { openDialog = true }) {
        Icon(Icons.Default.Add, contentDescription = "Favorite")
    }
    if(openDialog){
        Dialog(
            onDismissRequest = {
                openDialog = false
                onSubmit()
            }
        ){
            Surface(
                modifier = Modifier
//                    .height(350.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp)),
            ) {
                Column {
                    Row {
                            AutoCompleteArticle(update){
                                articleExist = it
                        }
                    }
                    Row {
                        InputTextField(
                            labelValue = if (quantity == 0.0) "" else quantity.toString(),
                            label = "Quantity",
                            singleLine = true,
                            maxLine = 1,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction =  ImeAction.Next),
                            onValueChange = {
                                quantity = it.toDouble()
                            }
                            , onImage = {}
                        ) {

                        }
                    }
                    Row{
                        InputTextField(
                            labelValue = if (discount == 0.0) "" else discount.toString(),
                            label = "Discount",
                            singleLine = true,
                            maxLine = 1,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction =  ImeAction.Next),
                            onValueChange = {
                                discount = it.toDouble()
                            }
                            , onImage = {}
                        ) {

                        }
                    }

                    Row {
                        Row (
                            modifier = Modifier.weight(1f)
                        ){

                            ButtonSubmit(labelValue = "ok", color = Color.Green, enabled = !(quantity == 0.0 || !articleExist)) {
                                    val command = invoiceViewModel.commandLineDto.copy()
                                    command.quantity = quantity
                                    command.discount = discount
                                    command.article = invoiceViewModel.article
                                if(update) {
                                    invoiceViewModel.commandLineDtos -= invoiceViewModel.commandLineDto
                                }
                                    command.totTva =
                                        quantity * command.article?.article?.tva!! * invoiceViewModel.article.sellingPrice!! / 100
                                    command.prixArticleTot =
                                        quantity * command.article?.sellingPrice!!*(1-command.discount!!/100)
                                    command.invoice?.code =
                                        invoiceViewModel.lastInvoiceCode
                                    invoiceViewModel.commandLineDtos += command
                                    invoiceViewModel.commandLineDto = CommandLineDto()
                                    invoiceViewModel.article = ArticleCompanyDto()
                                    quantity = 0.0
                                    discount = 0.0
                                    openDialog = false
                                      onSubmit()

                            }
                        }
                        Row (
                            modifier = Modifier.weight(1f)
                        ){

                            ButtonSubmit(labelValue = "cancel", color = Color.Red, enabled = true) {
                                onSubmit()
                                openDialog = false
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShowPaymentDailog(ttc: BigDecimal, openDailog: Boolean, onSelected: (BigDecimal,Boolean) -> Unit) {
    var openDialog by remember { mutableStateOf(openDailog) }

    var monyInput by remember { mutableStateOf("") }
    var mony by remember { mutableStateOf(BigDecimal.ZERO) }
    var rest by remember { mutableStateOf(ttc) }
    var isEnabled by remember {
        mutableStateOf(false)
    }
    val decimalFormat = DecimalFormat("#.##")

    LaunchedEffect(key1 = mony) {
        rest = ttc - mony
        isEnabled = mony != BigDecimal.ZERO
    }

    if (openDialog) {
        Dialog(
            onDismissRequest = {
                openDialog = false
            }
        ) {
            Surface(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp)),
            ) {
                Column {
                    Row {
                        // InputTextField for 'monyInput'
                        TextField(
                            value = monyInput,
                            onValueChange = { inputValue ->
                                monyInput = inputValue
                                mony = inputValue.toBigDecimalOrNull() ?: BigDecimal.ZERO
                            },
                            label = { Text("mony") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Row {
                        // Display rest value without trailing zeros
                        Text(
                            text = decimalFormat.format(rest),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Row {

                        Row(
                            modifier = Modifier.weight(1f)
                        ) {
                            Button(
                                onClick = {
                                     if (rest>BigDecimal.ZERO){
                                        onSelected(mony,false)
                                    }else{
                                        onSelected(ttc,true)
                                    }
                                          },
                                colors = ButtonDefaults.buttonColors(Color.Green),
                                modifier = Modifier.fillMaxWidth(),
                                enabled = isEnabled
                            ) {
                                Text("Paye")
                            }
                        }
                        Row(
                            modifier = Modifier.weight(1f)
                        ) {
                            Button(
                                onClick = { onSelected(mony,false) },
                                colors = ButtonDefaults.buttonColors(Color.Red),
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isEnabled
                            ) {
                                Text("credit")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionSettingsDialog(onDismiss : () -> Unit) {
    val context = LocalContext.current
    AlertDialog(onDismissRequest =  onDismiss,
        title =  {
            Text(text = "Permission Required")
        },
        text = {
            Text(text = "this app needs location permission to function properly, please enable it in the app settings")
        },
        confirmButton = {
            TextButton(onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package",context.packageName,null)
                }
                context.startActivity(intent)
                onDismiss()
            }) {
                    Text(text = "Go to settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss)
            {
                Text(text = "Cancel")
            }
        }
    )
}

@Composable
fun DateFilterUI(paymentViewModel: PaymentViewModel, onDate : (String, String) -> Unit) {
    // State to hold the selected date
    val selectedDate = remember { mutableStateOf("From date") }
    val finDate = remember { mutableStateOf("To date") }
    // State to open the Date Picker dialog
    val openDialog = remember { mutableStateOf(false) }
    var openFinDialog by remember { mutableStateOf(false) }
    // Get current context and calendar instance
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    // Create a DatePickerDialog
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            // Update the calendar with the selected date
            calendar.set(year, month, dayOfMonth)
            // Format the selected date as "yyyy-MM-dd"
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDate.value = format.format(calendar.time)
            if(finDate.value == "To date"){
                finDate.value = selectedDate.value
            }
            onDate(selectedDate.value, finDate.value)
            openDialog.value = false
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    val dateFinPickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            // Update the calendar with the selected date
            calendar.set(year, month, dayOfMonth)
            // Format the selected date as "yyyy-MM-dd"
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            finDate.value = format.format(calendar.time)
            if(selectedDate.value == "To date"){
                selectedDate.value = finDate.value
            }
            onDate(selectedDate.value, finDate.value)
            openFinDialog = false
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    // UI to show the selected date and open date picker
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {

        Text(
            text = selectedDate.value,
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
                .clickable {
                    openDialog.value = true
                }, // Open date picker on click
            style = MaterialTheme.typography.bodySmall,
            color = Color.Blue
        )
            Text(
                text = finDate.value,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
                    .clickable {
                        openFinDialog = true
                    }, // Open date picker on click
                style = MaterialTheme.typography.bodySmall,
                color = Color.Blue
            )
        }

        if (openDialog.value) {
            datePickerDialog.show()
        }
        if(openFinDialog){
            dateFinPickerDialog.show()
        }

    }
}












