package com.aymen.metastore.ui.screen.admin

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.dependencyInjection.BASE_URL
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.ArticleViewModel
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel
import com.aymen.metastore.ui.component.ArticleCardForAdmin
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.LodingShape
import com.aymen.metastore.ui.component.addQuantityDailog
import kotlinx.coroutines.delay

@Composable
fun ArticleScreen() {
    val appViewModel : AppViewModel = hiltViewModel()
    val articleViewModel : ArticleViewModel = hiltViewModel()
    val adminArticles = articleViewModel.adminArticles.collectAsLazyPagingItems()
    val context = LocalContext.current
    var art by remember {
        mutableStateOf(ArticleCompany())
    }
    var articleIndex by remember {
        mutableIntStateOf(-1)
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
            ){
                ButtonSubmit(labelValue = "Add New", color = Color.Green, enabled = true) {
                    appViewModel.updateShow("ADD_ARTICLE")
                }
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth()
            ){
                LazyColumn {
                    items(
                       count =  adminArticles.itemCount,
                        key = adminArticles.itemKey{ it.id!! },
                    ){ index ->
                        val articleCompany = adminArticles[index]
                        var addQuantity by remember {
                            mutableStateOf(false)
                        }
                        if(articleCompany != null) {
                            SwipeToDeleteContainer(
                                articleCompany,
                                onDelete = {item ->
                                    articleViewModel.deleteArticle(item)
                                },
                                onUpdate = {item ->
                                    articleViewModel.assignarticleCompany(item)
                                    appViewModel.updateShow("ADD_ARTICLE_FOR_COMPANY")

                                }
                            ) { article ->
                                ArticleCardForAdmin(
                                    articleCompany,
                                    image = "${BASE_URL}werehouse/image/${article.article?.image}/article/${article.article?.category!!.ordinal}"
                                ) {
                                    art = article
                                    addQuantity = true
                                }
                                if (addQuantity) {
                                    addQuantityDailog(art, addQuantity) {quantity ->
                                        if(quantity != 0.0) {
                                            articleViewModel.addQuantityArticle(
                                                quantity,
                                                article.id!!
                                            )
                                        }
                                        addQuantity = false
                                        articleIndex = -1
                                    }
                                }
                            }
                        }
                    }
                    adminArticles.loadState.apply {
                        if (refresh is LoadState.Loading) {
                            item {
                                LodingShape()
                            }
                        }
                        if (refresh is LoadState.Error) {
                            item {
                                Toast.makeText(context,
                                    (refresh as LoadState.Error).error.localizedMessage, Toast.LENGTH_SHORT).show()
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
fun DeleteArticle(
    swipeDismissState : SwipeToDismissBoxState
) {
    val color = when(swipeDismissState.dismissDirection){
        SwipeToDismissBoxValue.EndToStart ->Color.Red
        SwipeToDismissBoxValue.StartToEnd -> Color.Green
        SwipeToDismissBoxValue.Settled -> Color.Transparent
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color)
        .padding(16.dp),
        contentAlignment = Alignment.CenterEnd
    ){
        Icon(imageVector = if(swipeDismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)Icons.Default.Delete else Icons.Filled.Update
            , contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun <T> SwipeToDeleteContainer(
    item : T,
    onDelete: (T) -> Unit,
    onUpdate: (T) -> Unit,
    animationDuration: Int = 500,
    content : @Composable (T) -> Unit
){
    var isRemoved by remember {
        mutableStateOf(false)
    }
    var showDialog by remember {
        mutableStateOf(false)
    }
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = {value ->
            if(value == SwipeToDismissBoxValue.EndToStart){
                showDialog = true

            }
            if(value == SwipeToDismissBoxValue.StartToEnd){
                onUpdate(item)
            false
        }
            else{
                false
            }
        }
    )
    LaunchedEffect(key1 = isRemoved) {
        if(isRemoved){
            delay(animationDuration.toLong())
        }
    }
    AnimatedVisibility(visible = !isRemoved,
        exit = shrinkVertically (
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismissBox(
            state = state,
            backgroundContent = { DeleteArticle(swipeDismissState = state) },
            enableDismissFromEndToStart = true,
            content = {content(item)}
        )

    }
    if (showDialog) {
        ShowConfirmDialog { confirmed ->
            showDialog = false
            if (confirmed) {
                // Proceed with deletion if the user confirms
                isRemoved = true
                onDelete(item)
            }
        }
    }
}

@Composable
fun ShowConfirmDialog( onConfirm: (Boolean) -> Unit) {
    var showDialog by remember {
        mutableStateOf(false)
    }
    AlertDialog(
        onDismissRequest = { showDialog = false },
        title = { Text(text = "Confirm Deletion") },
        text = { Text(text = "Are you sure to delete this item?") },
        confirmButton = {
            Button(
                onClick = {
                    showDialog = false
                    // Return true if user confirms deletion
                    // This will trigger the deletion process in SwipeToDeleteContainer
                    onConfirm(true)
                }
            ) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    showDialog = false
                    // Return false if user cancels deletion
                    // This will cancel the deletion process in SwipeToDeleteContainer
                    onConfirm(false)
                }
            ) {
                Text(text = "Cancel")
            }
        }
    )
}




