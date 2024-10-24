package com.aymen.store.ui.component

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.metastore.model.entity.realm.User
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.entity.converterRealmToApi.mapApiArticleToRealm
import com.aymen.store.model.entity.converterRealmToApi.mapArticleCompanyToRealm
import com.aymen.store.model.entity.realm.Article
import com.aymen.store.model.entity.realm.Company
import com.aymen.store.model.repository.ViewModel.ArticleViewModel
import com.aymen.store.model.repository.ViewModel.ClientViewModel
import com.aymen.store.model.repository.ViewModel.InvoiceViewModel
import kotlin.coroutines.coroutineContext


@Composable
fun AutoCompleteClient(update : Boolean, onClientSelected : (Boolean) -> Unit) {
    val clientViewModel: ClientViewModel = hiltViewModel()
    val invoiceViewModel: InvoiceViewModel = hiltViewModel()

    val clientType = invoiceViewModel.clientType
    val focusRequester = remember { FocusRequester() }
    // Fetch the clients when the composable is launched
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Collect clients as state from ViewModel
    val clients by clientViewModel.myClients.collectAsState(emptyList())

    var clientname by remember {
        mutableStateOf("")
    }
    var expanded by remember {
        mutableStateOf(false)
    }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(key1 = update) {
        if(clientType == AccountType.USER && update){
        clientname = invoiceViewModel.clientUser.username
        }
        if(clientType == AccountType.COMPANY && update){
            clientname = invoiceViewModel.clientCompany.name
        }
    }
    Column(
        modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    expanded = false
                }
            )
    ) {
        Text(
            text = "Client",
            modifier = Modifier.padding(start = 3.dp, bottom = 2.dp),
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp) // heigtTextField
                        .border(
                            width = 1.8.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .onGloballyPositioned {
                            textFieldSize = it.size.toSize()
                        }
                        .focusRequester(focusRequester),
                    value = clientname,
                    onValueChange = {
                        onClientSelected(false)
                        clientname = it
                        expanded = true
                        if (it.isNotEmpty()) {
                            clientViewModel.getAllMyClientContaining(it)
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Black
                    ),
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowDropDown,
                                contentDescription = null
                            )
                        }
                    }
                )
            }

            AnimatedVisibility(visible = expanded) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .width(textFieldSize.width.dp),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 150.dp)
                    ) {
                        if (clientname.isNotEmpty()) {
                            items(
                                clients.filter {
                                    val clientName = it.person?.username?.lowercase() ?: it.client?.name?.lowercase()
                                    val query = clientname.lowercase()
                                    // Safely check if client name contains the query
                                    (clientName?.contains(query)
                                        ?: clientName?.contains("others")) == true
                                }.sortedBy {
                                    it.client?.name // Safely access client name
                                }
                            ) { clientRelation ->
                                clientRelation.client?.let { client ->
                                    ClientItem(client = client) { selectedClient ->
                                        clientname = selectedClient.name
                                        invoiceViewModel.clientCompany = selectedClient
                                        invoiceViewModel.clientType = AccountType.COMPANY
                                        onClientSelected(true)
                                        expanded = false
                                    }
                                }
                                clientRelation.person?.let { client ->
                                    ClientUserItem(client = client) { selectedClient ->
                                        clientname = selectedClient.username
                                        invoiceViewModel.clientUser = selectedClient
                                        invoiceViewModel.clientType = AccountType.USER
                                        onClientSelected(true)
                                        expanded = false
                                    }
                                }
                            }
                        } else {
                            expanded = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClientItem(
    client : Company,
    onSelect : (Company) -> Unit
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelect(client)
            }
            .padding(10.dp)
    ){
        Text(text = client.name, fontSize = 16.sp)

    }
}

@Composable
fun ClientUserItem(
    client : User,
    onSelect : (User) -> Unit
) {
    Log.e("aymenbabayclient","client name = ${client.username}")
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelect(client)
            }
            .padding(10.dp)
    ){
        Text(text = client.username, fontSize = 16.sp)

    }
}



////////////////////////////////
@Composable
fun AutoCompleteArticle(update : Boolean ,onSelcetArticle : (Boolean) -> Unit) {
    val articleViewModel : ArticleViewModel = viewModel()
    val invoiceViewModel : InvoiceViewModel = viewModel()
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
    val articles by articleViewModel.response.collectAsState(initial = emptyList())
    var articleLibel by remember {
        mutableStateOf("")
    }
    LaunchedEffect(key1 = update) {
        if (update) {
            articleLibel = invoiceViewModel.commandLineDto.article?.article?.libelle ?: ""
        }
    }
    val heigtTextField by remember {
        mutableStateOf(55.dp)
    }
    var textFieldSize by remember {
        mutableStateOf(Size.Zero)
    }
    var expanded by remember {
        mutableStateOf(false)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }

    Column(
        modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    expanded = false
                }
            )
    ) {

        Text(
            text = "com.aymen.metastore.model.entity.room.Article",
            modifier = Modifier.padding(start = 3.dp, bottom = 2.dp),
            // fontSize = 16.dp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row (
                modifier = Modifier.fillMaxWidth()
            ){
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(heigtTextField)
                        .border(
                            width = 1.8.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .onGloballyPositioned {
                            textFieldSize = it.size.toSize()
                        }
                        .focusRequester(focusRequester),
                    value = articleLibel,
                    onValueChange ={
                        articleLibel = it
                        onSelcetArticle(false)
                        expanded = true
                        if(it.isNotEmpty()){
                        articleViewModel.getAllMyArticleContaining(it)
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Black
                    ),
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowDropDown,
                                contentDescription = null
                            )
                        }
                    }
                )
            }

            AnimatedVisibility(visible = expanded) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .width(textFieldSize.width.dp),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    LazyColumn (
                        modifier = Modifier.heightIn(max = 150.dp)
                    ){

                        if(articleLibel.isNotEmpty()){
                            if(!update) {
                                items(
                                    articles.filterNot { article ->
                                        invoiceViewModel.commandLineDtos.any { commandLineDto ->
                                            commandLineDto.article?.article?.libelle == article.article!!.libelle
                                        }
                                    }
                                        .sortedBy {
                                            it.article!!.libelle
                                        }
                                ) {
                                    ArticleItem(article = it) { article ->
                                        articleLibel = article.article!!.libelle
                                        expanded = false
                                        invoiceViewModel.article = article
                                        onSelcetArticle(true)
                                    }
                                }
                            }else{
                                items(
                                    articles
                                        .sortedBy {
                                            it.article!!.libelle
                                        }
                                ) {
                                    ArticleItem(article = it) { article ->
                                        articleLibel = article.article!!.libelle
                                        expanded = false
                                        invoiceViewModel.article = article
                                        onSelcetArticle(true)
                                    }
                                }
                            }
                        }else{
//                            items(
//                                articles
//                            ){
//                                ArticleItem(article = com.aymen.metastore.model.entity.room.Article()) { title ->
//
//                                }
//                            }
                                    expanded = false
                        }
                    }
                }
            }

        }

    }
}

@Composable
fun ArticleItem(
    article : ArticleCompany,
    onSelect : (ArticleCompany) -> Unit
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelect(article)
            }
            .padding(10.dp)
    ){

        Text(text = article.article!!.libelle, fontSize = 16.sp)

    }
}

////////////////////////////////

//@Composable
//fun AutoCompleteCompanyCategory() {
//    var category by remember {
//        mutableStateOf("")
//    }
//    val heigtTextField by remember {
//        mutableStateOf(55.dp)
//    }
//    var textFieldSize by remember {
//        mutableStateOf(Size.Zero)
//    }
//    var expanded by remember {
//        mutableStateOf(false)
//    }
//    val interactionSource = remember {
//        MutableInteractionSource()
//    }
//    Column(
//        modifier = Modifier
//            .padding(30.dp)
//            .fillMaxWidth()
//            .clickable(
//                interactionSource = interactionSource,
//                indication = null,
//                onClick = {
//                    expanded = false
//                }
//            )
//    ) {
//        Text(
//            text = "com.aymen.metastore.model.entity.room.Company com.aymen.metastore.model.entity.room.Category",
//            modifier = Modifier.padding(start = 3.dp, bottom = 2.dp),
//            // fontSize = 16.dp,
//            color = Color.Black,
//            fontWeight = FontWeight.Medium
//        )
//        Column(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Row (
//                modifier = Modifier.fillMaxWidth()
//            ){
//                TextField(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(heigtTextField)
//                        .border(
//                            width = 1.8.dp,
//                            color = Color.Black,
//                            shape = RoundedCornerShape(15.dp)
//                        )
//                        .onGloballyPositioned {
//                            textFieldSize = it.size.toSize()
//                        },
//                    value = category,
//                    onValueChange ={
//                        category = it
//                        expanded = true
//                    },
//                    colors = TextFieldDefaults.colors(
//                        focusedIndicatorColor = Color.Transparent,
//                        unfocusedIndicatorColor = Color.Transparent,
//                        cursorColor = Color.Black
//                    ),
//                    textStyle = TextStyle(
//                        color = Color.Black,
//                        fontSize = 16.sp
//                    ),
//                    keyboardOptions = KeyboardOptions(
//                        keyboardType = KeyboardType.Text,
//                        imeAction = ImeAction.Done
//                    ),
//                    singleLine = true,
//                    trailingIcon = {
//                        IconButton(onClick = { expanded = !expanded }) {
//                            Icon(
//                                imageVector = Icons.Rounded.ArrowDropDown,
//                                contentDescription = null
//                            )
//                        }
//                    }
//                )
//            }
//
//            AnimatedVisibility(visible = expanded) {
//                Card(
//                    modifier = Modifier
//                        .padding(horizontal = 5.dp)
//                        .width(textFieldSize.width.dp),
//                    elevation = CardDefaults.cardElevation(10.dp)
//                ) {
//                    LazyColumn (
//                        modifier = Modifier.heightIn(max = 150.dp)
//                    ){
//
//                            items(
//                                categories.filter {
//
//
//
//                                }
//                                    .sortedBy {
//                                        it.libelle
//                                    }
//                            ){
//
//                                    expanded = false
//
//                            }
//
//                    }
//                }
//            }
//
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dropDownCompanyCategory(onSelect: (CompanyCategory) -> Unit) {

    val categories = CompanyCategory.entries
    var itemSelected by remember {
        mutableStateOf(categories[0])
    }
    var isExpanded by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = Modifier.wrapContentHeight()
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = { isExpanded = !isExpanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    value = itemSelected.toString(),
                    onValueChange = {
                                    onSelect(itemSelected)
                    },
                    readOnly = true,
                    colors =  TextFieldDefaults.colors(
                        focusedLabelColor = Color.Black,
                        cursorColor = Color.Magenta
                    ),
//                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) } this to display the icon at the end
                    leadingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }

                )

                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }) {
                    categories.forEachIndexed { index, text ->
                        DropdownMenuItem(
                            text = { Text(text.toString()) },
                            onClick = {
                                itemSelected = categories[index]
                                onSelect(itemSelected)
                                Log.e("companycateg","item selected $itemSelected")

                                isExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
    }
//    return  itemSelected
}
