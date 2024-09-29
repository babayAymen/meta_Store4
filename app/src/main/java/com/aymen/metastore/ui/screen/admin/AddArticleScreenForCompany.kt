package com.aymen.metastore.ui.screen.admin

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aymen.store.model.entity.realm.Article

@Composable
fun AddArticleScreenForCompany(article : Article){
    Text(text = article.libelle)
}