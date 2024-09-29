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
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.ArticleViewModel
import com.aymen.store.ui.component.ArticleCard

@Composable
fun ArticlesScreenForCompanyByCategory(){
    val articleViewModel : ArticleViewModel = hiltViewModel()
    val appViewModel : AppViewModel = hiltViewModel()
    LaunchedEffect(key1 = Unit) {
        articleViewModel.getAllArticlesByCategory()
    }
    var add by remember {
        mutableStateOf(false)
    }
val articles by articleViewModel.articles.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn {
            items(articles){
                ArticleCard(article = it){
                    add = true
                    articleViewModel.article = it
                }
                if(add) {
                    appViewModel.updateShow("add article for company")
                }
            }
        }
    }
}