package com.aymen.metastore.ui.component

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.map
import com.aymen.metastore.R
import com.aymen.metastore.model.Enum.InvoiceDetailsType
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.entity.model.Article
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.Category
import com.aymen.metastore.model.entity.model.ClientProviderRelation
import com.aymen.metastore.model.entity.model.CommandLine
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.Inventory
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.model.SubCategory
import com.aymen.metastore.dependencyInjection.BASE_URL
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.Enum.PrivacySetting
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.Enum.UnitArticle
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.CategoryViewModel
import com.aymen.metastore.model.repository.ViewModel.CompanyViewModel
import com.aymen.metastore.model.repository.ViewModel.InvoiceViewModel
import com.aymen.metastore.model.repository.ViewModel.PaymentViewModel
import com.aymen.metastore.model.repository.ViewModel.SubCategoryViewModel
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
fun dropDownItems(unit : UnitArticle ,list: List<UnitArticle>):UnitArticle {
    var itemSelected by remember {
        mutableStateOf(unit)
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
fun DropDownCategory(category : Category? , pagingItems: LazyPagingItems<Category>, onSelected: (Category) -> Unit) {
    val categoryViewModel: CategoryViewModel = hiltViewModel()
    if (pagingItems.itemCount != 0) {
        var itemSelected by remember {
            mutableStateOf(
             category
            )
        }
        if(category?.id == null) {
            itemSelected =  pagingItems.peek(0)
        }
        onSelected(itemSelected!!)
     //   categoryViewModel.category = itemSelected
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
                        value = itemSelected?.libelle ?: "",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
                    )

                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false }
                    ) {
                        for (index in 0 until pagingItems.itemCount) {
                            val item =
                                pagingItems.peek(index) // Avoids loading the item unnecessarily
                            if (item != null) {
                                DropdownMenuItem(
                                    text = { Text(item.libelle ?: "Unknown") },
                                    onClick = {
                                        onSelected(item)
                                        itemSelected = item
                                        isExpanded = false
                                        categoryViewModel.category = itemSelected!!
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownSubCategory(subCategory : SubCategory, list : LazyPagingItems<SubCategory>, categoryId : Long, onSelected : (SubCategory) -> Unit) {

    val subCategoryViewModel : SubCategoryViewModel = hiltViewModel()
    var itemSelectedLibell by remember {
        mutableStateOf(subCategory.libelle)
    }
    var itemSelected by remember {
        mutableStateOf(SubCategory())
    }
    if(list.itemCount != 0){
        itemSelected = list.peek(0)!!
        itemSelectedLibell = itemSelected.libelle?:""
    }else{
        itemSelected = SubCategory()
        itemSelectedLibell = "select sub category"
    }
    onSelected(itemSelected)
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
                    value = itemSelectedLibell?:"select sub category",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
                )

                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }
                ) {
                    list.itemSnapshotList.items.forEach { subCategory ->
                        if (subCategory.category?.id == categoryId) {
                            DropdownMenuItem(
                                text = { Text(subCategory.libelle!!) },
                                onClick = {
                                    itemSelectedLibell = subCategory.libelle!!
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

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownCompany(provider : Company? ,list: LazyPagingItems<ClientProviderRelation>, onSelected: (Company) -> Unit) {
    val companyViewModel: CompanyViewModel = hiltViewModel()
    if (list.itemCount != 0) {
        var itemSelected by remember {
            mutableStateOf(provider)
        }
        if(provider?.id == null) {
            itemSelected =  list.peek(0)?.provider
        }
        onSelected(itemSelected!!)
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
                        value = itemSelected?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
                    )

                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false }) {
                        for (index in 0 until list.itemCount) {
                            val relation = list.peek(index)
                            if (relation != null){
                            DropdownMenuItem(
                                text = { Text(relation.provider?.name!!) },
                                onClick = {
                                    onSelected(relation.provider!!)
                                    itemSelected = relation.provider
                                    isExpanded = false
                                    companyViewModel.providerId = itemSelected?.id ?: 0
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
fun ArticleCardForAdmin(article : ArticleCompany, image : String, onSelected: () -> Unit) {
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
                    NormalText(value = article.article?.libelle?:"", aligne = TextAlign.Start)
                    article.article?.code?.let { NormalText(value = it, aligne = TextAlign.Start) }
                    NormalText(value = article.unit.toString(), aligne = TextAlign.Start)
                    NormalText(value = article.article?.tva.toString(), aligne = TextAlign.Start)
                    NormalText(value = article.sellingPrice.toString(), aligne = TextAlign.Start)
                    NormalText(value = article.quantity.toString(), aligne = TextAlign.Start)
                    NormalText(value = article.minQuantity.toString(), aligne = TextAlign.Start)
                    article.article?.barcode?.let { NormalText(value = it, aligne = TextAlign.Start) }
                    article.article?.discription?.let { NormalText(value = it, aligne = TextAlign.Start) }

                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        article.article?.tva?.let { ShowPrice(cost = article.cost?:0.0, margin = article.sellingPrice!!, tva = it) }
                        ArticleDetails(value = article.quantity.toString(), aligne = TextAlign.Start)
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
fun addQuantityDailog(article: ArticleCompany, openDailoge: Boolean, onSubmit: (Double) -> Unit) {
    var openDialog by remember { mutableStateOf(openDailoge) }
    var quantity by remember {
        mutableDoubleStateOf(0.0)
    }
    var rawInput by remember {
        mutableStateOf("")
    }
    if (openDialog) {
        Dialog(
            onDismissRequest = {
                openDialog = false
                onSubmit(0.0)
            }
        ) {
            Surface(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp)),
            ) {
                Column {
                    NormalText(value = "Add Quantity", aligne = TextAlign.Center)
                    Spacer(modifier = Modifier.padding(10.dp))
                    Row {
                        Row(
                            modifier = Modifier.weight(1f)
                        ) {
                            NormalText(value = article.article?.libelle!!, aligne = TextAlign.Start)
                        }
                        Row (
                            modifier = Modifier.weight(1f)
                        ){
                            ShowImage(
                                image = "${BASE_URL}werehouse/image/${article.article?.image}/article/${
                                    article.company?.category?.ordinal
                                }"
                            )
                        }
                    }
                    InputTextField(
                        labelValue = rawInput,
                        label = "Quantity",
                        singleLine = true,
                        maxLine = 1,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Go,
                            autoCorrect = true
                        ),
                        onValueChange = {
                            if (article.unit == UnitArticle.U) {
                                if (it.matches(Regex("^[0-9]*$"))) {
                                    rawInput = it
                                    quantity = it.toDoubleOrNull() ?: 0.0
                                }
                            } else {
                                if (it.matches(Regex("^[0-9]*[,.]?[0-9]*$"))) {
                                    val normalizedInput = it.replace(',', '.')

                                    if (normalizedInput.startsWith(".")) {
                                        rawInput = normalizedInput
                                        quantity = 0.0
                                    } else if (normalizedInput.endsWith(".")) {
                                        rawInput = normalizedInput
                                        quantity = normalizedInput.dropLast(1).toDoubleOrNull() ?: 0.0
                                    } else {
                                        rawInput = normalizedInput
                                        quantity = normalizedInput.toDoubleOrNull() ?: 0.0
                                    }
                                }
                            }
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
                               onSubmit(0.0)
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
fun SubCategoryCardForAdmin(subCategory: SubCategory, image : String,
                            category: Category
) {
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
fun InventoryCard(inventory: Inventory, image : String) {
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
                    NormalText(value = inventory.article?.article?.libelle!!, aligne = TextAlign.Start)
                    inventory.article.article?.code?.let { NormalText(value = it, aligne = TextAlign.Start) }
                    NormalText(value = inventory.article.quantity.toString(), aligne = TextAlign.Start)

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
            Row {
             Column(
                 modifier = Modifier.weight(2f)
             ) {

            NormalText(value = article.libelle?:"", aligne = TextAlign.Start)
            NormalText(value = article.tva.toString(), aligne = TextAlign.Start)
            article.discription?.let { NormalText(value = it, aligne = TextAlign.Start) }
             }
                Column (
                    modifier = Modifier.weight(1f)
                ){
                    ShowImage(image = "${BASE_URL}werehouse/image/${article.image}/article/${
                        article.category?.ordinal}" )
                }
            }
            ButtonSubmit(labelValue = "add", color = Color.Green, enabled = true) {
                onSelected()
            }
        }
    }
}

@Composable
fun ClientCard(client: ClientProviderRelation, image : String) {
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
                       client.client?.let { NormalText(value = it.name, aligne = TextAlign.Start) }
                       client.client?.let { it.code?.let { it1 -> NormalText(value = it1, aligne = TextAlign.Start) } }
                    client.person?.username?.let { NormalText(value = it, aligne = TextAlign.Start) }
                    NormalText(value = client.mvt.toString(), aligne = TextAlign.Start)

                }
                Column(
                    modifier = Modifier
                        .weight(0.3f)
                        .align(Alignment.CenterVertically)
                ) {
                        Log.e("showImageclinetcard",image)
                    if(client.client?.logo != null || client.person?.image != null){
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
fun ProviderCard(provider: ClientProviderRelation, image : String) {
    Log.e("providerCard","log image provider : $image")
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
                    NormalText(value = provider.mvt.toString(), aligne = TextAlign.Start)

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

@Composable
fun InvoiceCard(invoice: Invoice, appViewModel: AppViewModel, invoiceViewModel: InvoiceViewModel, asProvider : Boolean) {
    Column(
        modifier = Modifier.padding(5.dp)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier
                .padding(4.dp)
                .clickable {
                    invoiceViewModel.invoice = invoice
                    invoiceViewModel.discount = invoice.discount
                    invoiceViewModel.invoiceMode = InvoiceMode.UPDATE
                    invoiceViewModel.invoiceType = invoice.type!!
                    if (invoice.type == InvoiceDetailsType.ORDER_LINE || invoice.status == Status.ACCEPTED || !asProvider) {
                        invoiceViewModel.invoiceMode = InvoiceMode.VERIFY
                    }
                    appViewModel.updateShow("add invoice")
                }
        ) {
            Row {
                Column (
                    modifier = Modifier.weight(0.7f)
                ){
                    NormalText(value = invoice.code.toString(), aligne = TextAlign.Center)
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
                            value = invoice.provider?.name!!,
                            aligne = TextAlign.Start
                        )
                    }
                    NormalText(value = invoice.prix_invoice_tot.toString(), aligne = TextAlign.End)

                }
            }
        }

    }
}

@Composable
fun TableRowContent(item: CommandLine) {
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
    var qte by remember {
        mutableDoubleStateOf(0.0)
    }
    var dct by remember {
        mutableDoubleStateOf(0.0)
    }
    var quantity by remember {
        mutableStateOf("")
    }
    var discount by remember {
        mutableStateOf("")
    }
    var articleExist by remember {
        mutableStateOf(false)
    }
    if(update){
        articleExist = true
        qte = invoiceViewModel.commandLineDto.quantity
        dct = invoiceViewModel.commandLineDto.discount?:0.0
    }
    val commandsLine by invoiceViewModel.commandLine.collectAsStateWithLifecycle()
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
                            labelValue = quantity,
                            label = "Quantity",
                            singleLine = true,
                            maxLine = 1,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction =  ImeAction.Next),
                            onValueChange = {
                                if (invoiceViewModel.article.unit == UnitArticle.U) {
                                    if (it.matches(Regex("^[0-9]*$"))) {
                                        quantity = it
                                        qte = it.toDoubleOrNull() ?: 0.0
                                    }
                                } else {
                                    if (it.matches(Regex("^[0-9]*[,.]?[0-9]*$"))) {
                                        val normalizedInput = it.replace(',', '.')
                                        quantity = normalizedInput
                                        qte = if (normalizedInput.startsWith(".") && normalizedInput.endsWith(".")) {
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
                            }
                            , onImage = {}
                        ) {

                        }
                    }
                    Row{
                        InputTextField(
                            labelValue = discount,
                            label = "Discount",
                            singleLine = true,
                            maxLine = 1,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction =  ImeAction.Next),
                            onValueChange = {
                                if (it.matches(Regex("^[0-9]*[,.]?[0-9]*$"))) {
                                    val normalizedInput = it.replace(',', '.')
                                    discount = normalizedInput
                                    dct = if (normalizedInput.startsWith(".") && normalizedInput.endsWith(".")) {
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
                            , onImage = {}
                        ) {

                        }
                    }

                    Row {
                        Row (
                            modifier = Modifier.weight(1f)
                        ){

                            ButtonSubmit(labelValue = "ok", color = Color.Green, enabled = !(qte == 0.0 || !articleExist)) {
                                    val command = invoiceViewModel.commandLineDto.copy()
                                    command.quantity = qte
                                    command.discount = qte
                                    command.article = invoiceViewModel.article
                                if(update) {
                                    invoiceViewModel.substructCommandsLine()
                                }
                                    command.totTva =
                                        qte * command.article?.article?.tva!! * invoiceViewModel.article.sellingPrice!! / 100
                                    command.prixArticleTot =
                                        qte * command.article?.sellingPrice!!*(1-command.discount!!/100)
                                    command.invoice?.code = invoiceViewModel.lastInvoiceCode
                                invoiceViewModel.addCommandLine(command)
                                    invoiceViewModel.commandLineDto = CommandLine()
                                    invoiceViewModel.article = ArticleCompany()
                                    qte = 0.0
                                    dct = 0.0
                                quantity = ""
                                discount = ""
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
fun ShowQuantityDailog(article : ArticleCompany, openDailog : Boolean,invoiceViewModel : InvoiceViewModel, update : Boolean, onSubmit: () -> Unit) {
    var openDialog by remember { mutableStateOf(openDailog) }
    var qte by remember {
        mutableDoubleStateOf(0.0)
    }
    var quantity by remember {
        mutableStateOf("")
    }
    var dct by remember {
        mutableDoubleStateOf(0.0)
    }
    var discount by remember {
        mutableStateOf("")
    }
    var isEnabled by remember {
        mutableStateOf(false)
    }
    val commandsLine by invoiceViewModel.commandLine.collectAsStateWithLifecycle()
    if(openDialog){
        Dialog(
            onDismissRequest = {
                openDialog = false
                onSubmit()
            }
        ) {
            Box{
                Column(modifier = Modifier.background(Color.White)) {
                    Row {
                        Row(modifier = Modifier.weight(1f)) {

                ShowImage(image = "${BASE_URL}werehouse/image/${article.article?.image}/article/${article.article?.category?.ordinal}")
                        }
                        Row (modifier = Modifier.weight(2f)){

                    Text(text = article.article?.libelle!!)
                        }
                    }
                    InputTextField(
                        labelValue = quantity,
                        label = "quantity",
                        singleLine = true,
                        maxLine = 1,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = {
                            if(article.unit == UnitArticle.U){
                                if (it.matches(Regex("^[0-9]*$"))) {
                                    quantity = it
                                    qte = it.toDoubleOrNull() ?: 0.0
                                }
                            }else if (it.matches(Regex("^[0-9]*[,.]?[0-9]*$"))) {
                                val normalizedInput = it.replace(',', '.')
                                quantity = normalizedInput
                                qte = if(normalizedInput.startsWith(".")) 0.0
                                else if (normalizedInput.endsWith(".")) {
                                    normalizedInput.let { inp ->
                                        if (inp.toDouble() % 1.0 == 0.0) inp.toDouble() else 0.0
                                    }
                                } else {
                                    normalizedInput.toDoubleOrNull() ?: 0.0
                                }
                            }

                            isEnabled = qte != 0.0
                                        },
                        onImage = {}
                    ) {

                    }
                    InputTextField(
                        labelValue = discount,
                        label = "discount",
                        singleLine = true,
                        maxLine = 1,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        onValueChange = {
                            if (it.matches(Regex("^[0-9]*[,.]?[0-9]*$"))) {
                                val normalizedInput = it.replace(',', '.')
                                discount = normalizedInput
                                dct = if(normalizedInput.startsWith(".")) 0.0
                                else if (normalizedInput.endsWith(".")) {
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
                        Row(modifier = Modifier.weight(1f)) {
                            ButtonSubmit(labelValue = "Ok", color = Color.Green, enabled = isEnabled) {
                                val command = invoiceViewModel.commandLineDto.copy()
                                command.quantity = qte
                                command.discount = dct
                                command.article = article
                                if(update) {
                                    invoiceViewModel.substructCommandsLine()
                                }
                                command.totTva = qte * article.article?.tva!! * article.sellingPrice!! / 100
                                command.prixArticleTot = qte * article.sellingPrice!!*(1-command.discount!!/100)
                                command.invoice?.code = invoiceViewModel.lastInvoiceCode
                                invoiceViewModel.addCommandLine(command)
                                invoiceViewModel.commandLineDto = CommandLine()
                                qte = 0.0
                                quantity = ""
                                dct = 0.0
                                discount = ""
                                openDialog = false
                                onSubmit()
                            }
                        }
                        Row (modifier = Modifier.weight(1f)){
                            ButtonSubmit(labelValue = "Cancel", color = Color.Red , enabled = true) {
                                openDialog = false
                                onSubmit()
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












