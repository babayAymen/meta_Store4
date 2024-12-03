package com.aymen.metastore.ui.screen.user

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
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.model.entity.model.Company
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.store.model.Enum.RoleEnum
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.ArticleViewModel
import com.aymen.metastore.model.repository.ViewModel.ClientViewModel
import com.aymen.metastore.model.repository.ViewModel.CompanyViewModel
import com.aymen.metastore.ui.component.ArticleCardForSearch
import com.aymen.metastore.ui.component.CompanyCard
import com.aymen.metastore.ui.component.SearchField
import com.aymen.metastore.ui.component.UserCard
import com.aymen.store.ui.navigation.RouteController
import com.aymen.store.ui.navigation.Screen

@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    val companyViewModel : CompanyViewModel = hiltViewModel()
    val clientViewModel : ClientViewModel = hiltViewModel()
    val articleViewModel  : ArticleViewModel = hiltViewModel()
    val appViewModel : AppViewModel = hiltViewModel()
    val sharedViewModel : SharedViewModel = hiltViewModel()
    val histories = clientViewModel.histories.collectAsLazyPagingItems()
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

    val com =  companyViewModel.allCompanies.collectAsLazyPagingItems()
    val cli = clientViewModel.searchPersons.collectAsLazyPagingItems()
    val companySearch by articleViewModel.companyComment.collectAsStateWithLifecycle()
    val userSearch by articleViewModel.userComment.collectAsStateWithLifecycle()
    val client = clientViewModel.myClients.collectAsLazyPagingItems()

    val art = articleViewModel.article
    val articleCompany = articleViewModel.articleCompany
    LaunchedEffect(key1 = Unit) {
        clientViewModel.getAllSearchHistory()
    }
    LaunchedEffect(key1 = searchCategory) {
        searchType = SearchType.OTHER
    }
    LaunchedEffect( key1 = searchCategory, key2 = searchOption, key3 = searchType) {

        when(searchCategory){
            SearchCategory.COMPANY -> {
                when(searchType){
                    SearchType.OTHER ->{
                        Log.e("searchscreen","type company other")
                        companyViewModel.getAllCompaniesContaining(searchText,searchType,searchCategory)
                    }
                    SearchType.MY ->{
                        Log.e("searchscreen","type company my")
                        // والله لا تعرف كيفاه تمشي
                    }
                    else ->{
                        Log.e("searchscreen","type company else")
                        companyViewModel.getAllCompaniesContaining(searchText,searchType,searchCategory)
                    }
                }
            }
            SearchCategory.USER -> {
                Log.e("searchscreen","type user")
                        clientViewModel.getAllPersonContaining(searchText,searchType,searchCategory)
            }
            SearchCategory.ARTICLE-> {
                Log.e("searchscreen","type article")
                        articleViewModel.getAllMyArticleContaining(searchText,searchType)
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
                        if (show) {
                            item {
                                SearchItem(searchCategory) {
                                    searchCategory = it
                                }
                                if (role != RoleEnum.USER) {

                                    SearchTypeItem(appViewModel, searchType, searchCategory) {
                                        searchType = it
                                    }
                                }

                                when (searchCategory) {
                                    SearchCategory.COMPANY -> {
                                        for (index in 0 until com.itemCount) {
                                            val company = com[index]
                                            if(company != null){
                                            when (searchType) {
                                                SearchType.OTHER -> {
                                                    CompanyCard(
                                                        company.company!!
                                                    ) {
                                                        companyViewModel.myCompany = company.company
                                                        articleViewModel.companyId =
                                                            company.company.id!!
                                                        RouteController.navigateTo(Screen.CompanyScreen)
                                                        clientViewModel.saveHitory(
                                                            searchCategory,
                                                            company.id!!
                                                        )
                                                    }
                                                }

                                                else -> {
                                                    CompanyCard(
                                                        company.company!!
                                                    ) {
                                                        companyViewModel.myCompany = company.company
                                                        articleViewModel.companyId =
                                                            company.company.id!!
                                                        RouteController.navigateTo(Screen.CompanyScreen)
                                                        clientViewModel.saveHitory(
                                                            searchCategory,
                                                            company.company.id!!
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        }
                                    }

                                    SearchCategory.USER -> {
                                        for (index in 0 until cli.itemCount) {
                                            val user = cli[index]
                                            if (user != null) {
                                                Log.e("userrrr", "user search ${user.user}")
                                                UserCard(user.user!!, appViewModel) {
                                                    appViewModel._user.value = user.user
                                                    RouteController.navigateTo(Screen.UserScreen)
                                                    clientViewModel.saveHitory(
                                                        searchCategory,
                                                        user.user.id!!
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    SearchCategory.ARTICLE -> {
                                        val art =
                                            articleViewModel.searchArticles.collectAsLazyPagingItems()
                                        for (index in 0 until art.itemCount) {
                                            val article = art[index]
                                            ArticleCardForSearch(article!!) {
                                                companyViewModel.myCompany = article.company!!
//                                                    articleViewModel.articleCompany = article a determine
                                                RouteController.navigateTo(Screen.ArticleDetailScreen)
                                                clientViewModel.saveHitory(
                                                    searchCategory,
                                                    article.id!!
                                                )
                                            }
                                        }
                                    }

                                    else -> {

                                    }
                                }
                            }
                        }else {
                            items(count = histories.itemCount,
                                key = histories.itemKey{it.id!!}){index : Int ->
                                val history = histories[index]
                                    Column {
                                        when (history?.searchCategory) {
                                            SearchCategory.COMPANY -> {
                                                Log.e("search","company ${history.company}")
                                                CompanyCard(
                                                    history.company!!
                                                ) {
                                                    companyViewModel.myCompany = companySearch
                                                    articleViewModel.companyId = history.company.id!!
                                                    RouteController.navigateTo(Screen.CompanyScreen)
                                                    clientViewModel.saveHitory(
                                                        searchCategory,
                                                        history.id!!
                                                    )
                                                }
                                            }

                                            SearchCategory.USER -> {
                                                UserCard(history.user!!, appViewModel) {
                                                    appViewModel._user.value = history.user
                                                    RouteController.navigateTo(Screen.UserScreen)
                                                    clientViewModel.saveHitory(
                                                        searchCategory,
                                                        history.user.id!!
                                                    )
                                                }
                                            }

                                            SearchCategory.ARTICLE -> {
                                                ArticleCardForSearch(
                                                    history.article!!
                                                ) {
                                                    companyViewModel.myCompany = companySearch
                                                    articleViewModel.articleCompany = articleCompany
                                                    RouteController.navigateTo(Screen.ArticleDetailScreen)
                                                    clientViewModel.saveHitory(
                                                        searchCategory,
                                                        history.id!!
                                                    )
                                                }
                                            }

                                            else -> {
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
fun SearchTypeItem(appViewModel : AppViewModel, item : SearchType, searchCategory: SearchCategory, onClick : (SearchType) -> Unit) {
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
