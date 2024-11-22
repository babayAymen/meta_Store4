package com.aymen.metastore.ui.screen.admin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.ArticleViewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.ui.component.ArticleCard

@Composable
fun ArticlesScreenForCompanyByCategory(){
    val articleViewModel : ArticleViewModel = hiltViewModel()
    val appViewModel : AppViewModel = hiltViewModel()
    val sharedViewModel = hiltViewModel<SharedViewModel>()
    var add by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = Unit) {
        articleViewModel.getArticlesForCompanyByCompanyCategory(sharedViewModel.company.value.id!!, sharedViewModel.company.value.category!!)
    }
val articles = articleViewModel.articles.collectAsLazyPagingItems()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn {
            items(
                count = articles.itemCount,
                key = articles.itemKey{ it.id!! },
                ){index ->
                val article = articles[index]
                if(article != null) {
                    ArticleCard(article = article) {
                        add = true
                        articleViewModel.article = article
                    }
                    if (add) {
                        appViewModel.updateShow("add article for company")
                    }
                }
            }
        }
    }
}