package com.aymen.metastore.ui.component

import android.util.Log
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.entity.model.User
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.metastore.model.repository.ViewModel.ArticleViewModel
import com.aymen.metastore.model.repository.ViewModel.ClientViewModel
import com.aymen.metastore.model.repository.ViewModel.InvoiceViewModel
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.repository.ViewModel.ProviderViewModel


@Composable
fun AutoCompleteClient(update : Boolean, onClientSelected : (Boolean) -> Unit) {
    val clientViewModel: ClientViewModel = hiltViewModel()
    val invoiceViewModel: InvoiceViewModel = hiltViewModel()

    val clientType = invoiceViewModel.clientType
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val clients = clientViewModel.myClientsContainingForAutocomplete.collectAsLazyPagingItems()

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
        clientname = invoiceViewModel.clientUser.username?:""
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
                        .height(55.dp)
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
                            clientViewModel.getMyClientForAutocompleteClient(it)
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
                                count = clients.itemCount,
                                key = clients.itemKey { client -> client.id!! }
                            ) { index: Int ->
                                val client = clients[index]
                                if (client != null) {
                                    invoiceViewModel.setProviderCompany(client.provider!!)
                                    client.client?.let { clt ->
                                        ClientItem(client = clt) { selectedClient ->
                                            clientname = selectedClient.name
                                            invoiceViewModel.clientCompany = selectedClient
                                            invoiceViewModel.clientType = AccountType.COMPANY
                                            onClientSelected(true)
                                            expanded = false
                                        }
                                    }
                                    client.person?.let { clt ->
                                        ClientUserItem(client = clt) { selectedClient ->
                                            clientname = selectedClient.username!!
                                            invoiceViewModel.clientUser = selectedClient
                                            Log.e("testinvoice","client user id ${selectedClient.id}")
                                            invoiceViewModel.clientType = AccountType.USER
                                            onClientSelected(true)
                                            expanded = false
                                        }
                                    }
                                }
                            }
                        } else {
                            expanded = false
                            onClientSelected(false)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AutoCompleteProvider(update : Boolean, onClientSelected : (Boolean) -> Unit) {
    val providerViewModel: ProviderViewModel = hiltViewModel()
    val invoiceViewModel: InvoiceViewModel = hiltViewModel()

    val clientType = invoiceViewModel.clientType
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val clients = providerViewModel.virtualProviders.collectAsLazyPagingItems()
    val provider by invoiceViewModel.providerCompany.collectAsStateWithLifecycle()
    var providername by remember {
        mutableStateOf("")
    }
    var expanded by remember {
        mutableStateOf(false)
    }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(key1 = update) {
        if(update)
            providername = provider.name
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
            text = "Provider",
            modifier = Modifier.padding(start = 3.dp, bottom = 2.dp),
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .border(
                            width = 1.8.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .onGloballyPositioned {
                            textFieldSize = it.size.toSize()
                        }
                        .focusRequester(focusRequester),
                    value = providername,
                    onValueChange = {
                        onClientSelected(false)
                        providername = it
                        expanded = true
                        if (it.isNotEmpty()) {
                            providerViewModel.getAllMyProviders(it)
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
                        if (providername.isNotEmpty()) {
                            items(
                                count = clients.itemCount,
                                key = clients.itemKey { client -> client.id!! }
                            ) { index: Int ->
                                val client = clients[index]
                                if (client != null) {
                                client.provider?.let { clt ->
                                    ClientItem(client = clt) { selectedClient ->
                                        providername = selectedClient.name
                                        invoiceViewModel.clientCompany = client.client!!
                                        invoiceViewModel.setProviderCompany(selectedClient)
                                        invoiceViewModel.clientType = AccountType.COMPANY
                                        onClientSelected(true)
                                        expanded = false
                                    }
                                }
                            }
                        }
                        } else {
                            expanded = false
                            onClientSelected(false)
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
        Text(text = client.username!!, fontSize = 16.sp)

    }
}



////////////////////////////////
@Composable
fun AutoCompleteArticle(update : Boolean, asProvider : Boolean, providerId : Long ,onSelcetArticle : (ArticleCompany ,Boolean) -> Unit) {
    val articleViewModel : ArticleViewModel = hiltViewModel()
    val invoiceViewModel : InvoiceViewModel = hiltViewModel()
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
    val articles = articleViewModel.searchArticles.collectAsLazyPagingItems()
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
            text = "Article",
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
                        onSelcetArticle(ArticleCompany(),false)
                        expanded = true
                        if(it.isNotEmpty()){
                        articleViewModel.getAllMyArticleContaining(it,SearchType.MY, asProvider, providerId)
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

                                    count = articles.itemCount,
                                    key = articles.itemKey { client -> client.id!! },
                                    contentType = { articles.itemContentType { "articles" } }) { index: Int ->
                                    val article = articles[index]
                                    ArticleItem(article = article!!) { art ->
                                        articleLibel = art.article!!.libelle?:""
                                        expanded = false
                                        invoiceViewModel.article = art
                                        onSelcetArticle(art ,true)
                                    }
                                }
                            }else{
                                items(
                                    count = articles.itemCount,
                                    key = articles.itemKey{article -> article.id!!},
                                    contentType = {articles.itemContentType{"articles"}}
                                ) {index ->
                                    val article = articles[index]
                                    ArticleItem(article = article!!) { art ->
                                        articleLibel = art.article!!.libelle?:""
                                        expanded = false
                                        invoiceViewModel.article = art
                                        onSelcetArticle(art,true)
                                    }
                                }
                            }
                        }else{
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

        Text(text = article.article!!.libelle?:"", fontSize = 16.sp)

    }
}


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
