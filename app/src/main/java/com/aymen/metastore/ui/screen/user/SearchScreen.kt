package com.aymen.store.ui.screen.user

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aymen.metastore.model.entity.converterRealmToApi.mapRoomCompanyToCompanyDto
import com.aymen.metastore.model.entity.room.Article
import com.aymen.metastore.model.entity.room.Company
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.ArticleViewModel
import com.aymen.store.model.repository.ViewModel.ClientViewModel
import com.aymen.store.model.repository.ViewModel.CompanyViewModel
import com.aymen.store.ui.component.ArticleCardForSearch
import com.aymen.store.ui.component.CompanyCard
import com.aymen.store.ui.component.SearchField
import com.aymen.store.ui.component.UserCard
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen

@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    val companyViewModel : CompanyViewModel = hiltViewModel()
    val clientViewModel : ClientViewModel = hiltViewModel()
    val articleViewModel  : ArticleViewModel = hiltViewModel()
    val appViewModel : AppViewModel = hiltViewModel()
    val sharedViewModel : SharedViewModel = hiltViewModel()
    var show by remember {
        mutableStateOf(false)
    }
    var searchCategory by remember {
        mutableStateOf(SearchCategory.COMPANY)
    }
    var searchType by remember {
        mutableStateOf(SearchType.OTHER)
    }
    var searchText by remember {
        mutableStateOf("")
    }
    var searchOption by remember {
        mutableStateOf(false)
    }
    val role = appViewModel.userRole

    DisposableEffect(key1 = Unit) {
        onDispose {
            clientViewModel.histories = emptyList()
        }
    }
    val articles by articleViewModel.articlesByArticleId.collectAsStateWithLifecycle()
    val companies by companyViewModel.companiesByArticleId.collectAsStateWithLifecycle()
    val companySearch by articleViewModel.companyComment.collectAsStateWithLifecycle()
    val userSearch by articleViewModel.userComment.collectAsStateWithLifecycle()
    val client by clientViewModel.myClients.collectAsStateWithLifecycle()
    val art = articleViewModel.article
    val articleCompany = articleViewModel.articleCompany
    LaunchedEffect(key1 = Unit) {
        clientViewModel.getAllSearchHistory()
    }
    LaunchedEffect(key1 = searchCategory) {
        searchType = SearchType.OTHER
    }
    LaunchedEffect( key1 = searchCategory, key2 = searchOption, key3 = searchType) {
        companyViewModel.allCompanies = emptyList()
        clientViewModel.emptyClient()
        articleViewModel.searchArticles = emptyList()
        when(searchCategory){
            SearchCategory.COMPANY -> {
                when(searchType){
                    SearchType.OTHER ->{
                        companyViewModel.getAllCompaniesContaining(searchText)
                    }
                    SearchType.MY ->{
                        // والله لا تعرف كيفاه تمشي
                    }
                    else ->{
                        clientViewModel.getAllClientsCompanyContaining(searchText,searchType,searchCategory)
                    }
                }
            }
            SearchCategory.USER -> {
                        clientViewModel.getAllClientsUserContaining(searchText,searchType,searchCategory)
            }
            SearchCategory.ARTICLE-> {
                        articleViewModel.getAllArticlesContaining(searchText,searchType)
            }
            else ->{

            }
        }
    }
    Surface {
        Column {
            Row {
            SearchBar{isShow,isText,isSearch ->
                show = isShow
                searchText = isText
                searchOption = !isSearch
            }
            }
            Row {
                LazyColumn {
                    item {

                        if (show) {
                            SearchItem(searchCategory){
                                searchCategory = it
                            }
                            if(role != RoleEnum.USER){

                            SearchTypeItem(appViewModel,searchType,searchCategory){
                                searchType = it
                            }
                            }

                            when(searchCategory){
                                SearchCategory.COMPANY ->{
                                    when(searchType){
                                        SearchType.OTHER -> {
                                            companyViewModel.allCompanies.forEach {
                                                CompanyCard(it, companyViewModel, articleViewModel){
                                                    companyViewModel.myCompany = mapRoomCompanyToCompanyDto(it)
                                                    articleViewModel.companyId = it.id!!
                                                    RouteController.navigateTo(Screen.CompanyScreen)
                                                    clientViewModel.saveHitory(searchCategory,it.id)
                                                }
                                            }
                                        }
                                        else ->{
                                            client.forEach {
                                                CompanyCard(it.clientCompany!!, companyViewModel, articleViewModel){
                                                    companyViewModel.myCompany = mapRoomCompanyToCompanyDto(it.clientCompany)
                                                    articleViewModel.companyId = it.clientCompany.id!!
                                                    RouteController.navigateTo(Screen.CompanyScreen)
                                                    clientViewModel.saveHitory(searchCategory,it.clientCompany.id)
                                                }
                                            }
                                        }
                                    }
                                }
                                SearchCategory.USER -> {
                                    client.forEach{
                                    UserCard(it.clientUser!!,appViewModel){
                                        appViewModel._user.value = it.clientUser
                                        RouteController.navigateTo(Screen.UserScreen)
                                        clientViewModel.saveHitory(searchCategory,it.clientUser.id!!)
                                    }
                                        }
                                }
                                SearchCategory.ARTICLE -> {
                                            articleViewModel.searchArticles.forEach{
                                    companyViewModel.fetchCompanyByArticleId(it.id!!, it.companyId!!)
                                                articleViewModel.fetchArticlesByArticleId(it.id, it.articleId!!)
                                                val art = articles[it.articleId]
                                                val com = companies[it.companyId]
                                                ArticleCardForSearch( it, art?:Article(), com?:Company()){
                                                    companyViewModel.myCompany = mapRoomCompanyToCompanyDto(com!!)
                                                    articleViewModel.articleCompany = it
                                                    RouteController.navigateTo(Screen.ArticleDetailScreen)
                                                    clientViewModel.saveHitory(searchCategory,it.id)
                                                }
                                            }
                                }
                                else ->{

                                }
                            }
                        } else {
                            clientViewModel.histories.forEach{
                                when(it.searchCategory){
                                    SearchCategory.COMPANY -> {
                                        articleViewModel.getCompanyAndUserByIds(it.companyId!!,null)
                                        CompanyCard(companySearch, companyViewModel, articleViewModel){
                                            companyViewModel.myCompany = mapRoomCompanyToCompanyDto(companySearch)
                                            articleViewModel.companyId = it.companyId
                                            RouteController.navigateTo(Screen.CompanyScreen)
                                            clientViewModel.saveHitory(searchCategory,it.id!!)
                                        }
                                    }
                                    SearchCategory.USER ->{
                                            articleViewModel.getCompanyAndUserByIds(null,it.userId!!)
                                            UserCard(userSearch, appViewModel) {
                                                appViewModel._user.value = userSearch
                                                RouteController.navigateTo(Screen.UserScreen)
                                                clientViewModel.saveHitory(
                                                    searchCategory,
                                                    it.userId
                                                )
                                        }
                                    }
                                    SearchCategory.ARTICLE ->{
                                        articleViewModel.getArticleCompany(it.articleId!!)
                                        if(articleCompany.id != null) {
                                            articleViewModel.getCompanyAndUserByIds(
                                                articleCompany.companyId!!,
                                                null
                                            )
                                            articleViewModel.getArticleById(articleCompany.articleId)
                                        }
                                            ArticleCardForSearch(
                                                articleCompany,
                                                art,
                                                companySearch
                                            ) {
                                                companyViewModel.myCompany =
                                                    mapRoomCompanyToCompanyDto(companySearch)
                                                articleViewModel.articleCompany = articleCompany
                                                RouteController.navigateTo(Screen.ArticleDetailScreen)
                                                clientViewModel.saveHitory(searchCategory, it.id!!)
                                            }
                                    }
                                    else ->{
                                        Log.e("searchhistory", "search history other")
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun SearchBar(clickAction: (Boolean,String,Boolean) -> Unit) {

    Row{
        var searchText by remember {
            mutableStateOf("")
        }
    SearchField(label = "search",
        labelValue = searchText,
        value = {
            searchText = it
            clickAction(true,searchText,false)
        }
    ){
        if(searchText.isNotEmpty()){
        clickAction(true,searchText,true)
        }

    }
    }
}

@Composable
fun SearchItem(item : SearchCategory,onClick : (SearchCategory) -> Unit) {
    LazyRow(Modifier.fillMaxWidth()) {
        items(SearchCategory.entries){
            if(it != SearchCategory.OTHER){

            Text(text = it.toString(),
                Modifier
                    .padding(end = 20.dp)
                    .clickable {
                        onClick(it)
                    }
                    ,
                color = if(item == it) Color.Blue else Color.Black
            )
            }
        }
    }
}

@Composable
fun SearchTypeItem(appViewModel : AppViewModel,item : SearchType, searchCategory: SearchCategory, onClick : (SearchType) -> Unit) {
    LazyRow(Modifier.fillMaxWidth()) {
        items(SearchType.entries){
            if(searchCategory == SearchCategory.COMPANY && (it == SearchType.CLIENT || it == SearchType.PROVIDER || it == SearchType.OTHER)){
                Text(text = it.toString(),
                    Modifier
                        .padding(end = 20.dp)
                        .clickable {
                            onClick(it)
                        }
                ,
                color = if(item == it) Color.Blue else Color.Black
            )
            }
            if(searchCategory == SearchCategory.USER && (it == SearchType.CLIENT || it == SearchType.OTHER)){
                Text(text = it.toString(),
                    Modifier
                        .padding(end = 20.dp)
                        .clickable {
                            onClick(it)
                        }
                    ,
                    color = if(item == it) Color.Blue else Color.Black
                )
            }
            if(searchCategory == SearchCategory.ARTICLE && (it == SearchType.MY || it == SearchType.OTHER)){
                Text(text = it.toString(),
                    Modifier
                        .padding(end = 20.dp)
                        .clickable {
                            onClick(it)
                        }
                    ,
                    color = if(item == it) Color.Blue else Color.Black
                )
            }
        }
    }
}
