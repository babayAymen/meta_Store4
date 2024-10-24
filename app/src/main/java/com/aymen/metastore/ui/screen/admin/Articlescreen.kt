package com.aymen.store.ui.screen.admin

import android.util.Log
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.store.dependencyInjection.BASE_URL
import com.aymen.store.model.Enum.CompanyCategory
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.ArticleViewModel
import com.aymen.store.ui.component.ArticleCardForAdmin
import com.aymen.store.ui.component.ButtonSubmit
import com.aymen.store.ui.component.addQuantityDailog
import kotlinx.coroutines.delay

@Composable
fun ArticleScreen() {
    val appViewModel : AppViewModel = hiltViewModel()
    val articleViewModel : ArticleViewModel = hiltViewModel()
    val adminArticles by articleViewModel.adminArticles.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        articleViewModel.getAllMyArticlesApi()
    }
    var isSelected by remember {
        mutableStateOf(false)
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
                    appViewModel.updateShow("add article")
                }
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth()
            ){
                LazyColumn {
                    itemsIndexed(adminArticles){index , articleCompany ->
                        SwipeToDeleteContainer(
                            articleCompany,
                            onDelete = {
                                Log.e("aymenbabatdelete","delete")
                            },
                            appViewModel = appViewModel,
                        ){article ->
                            ArticleCardForAdmin(article = article,
                                image = "${BASE_URL}werehouse/image/${article.article!!.image}/article/${CompanyCategory.valueOf(article.company?.category!!).ordinal}"){
                                articleIndex = index
                                isSelected = true
                            }
                            if(isSelected && index == articleIndex){
                            addQuantityDailog(article,true){
                                articleViewModel.addQuantityArticle(it,article.id!!)
                                isSelected = false
                                articleIndex = -1
                            }
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
    val color = if(swipeDismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart){
        Color.Red
    }else Color.Transparent

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color)
        .padding(16.dp),
        contentAlignment = Alignment.CenterEnd
    ){
        Icon(imageVector = Icons.Default.Delete
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
    animationDuration: Int = 500,
    appViewModel: AppViewModel,
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
//                isRemoved = true
                showDialog = true
            }
            if(value == SwipeToDismissBoxValue.StartToEnd ) {
            // Swipe from start to end - add article
            appViewModel.updateShow("add article")
            false // No need to show confirmation, directly add the article
        }else{
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
            backgroundContent = { DeleteArticle(swipeDismissState = state)},
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
        text = { Text(text = "Are you sure you want to delete this item?") },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateArticle(
    swipeDismissState : SwipeToDismissBoxState
) {
    val color = if(swipeDismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd){
        Color.Green
    }else Color.Transparent

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color)
        .padding(16.dp),
        contentAlignment = Alignment.CenterEnd
    ){
        Icon(imageVector = Icons.Default.Update
            , contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun <T> SwipeToUpdateContainer(
    item : T,
    appViewModel: AppViewModel,
    animationDuration: Int = 500,
    content : @Composable (T) -> Unit
){
    var isUpdated by remember {
        mutableStateOf(false)
    }



    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = {value ->
            if(value == SwipeToDismissBoxValue.StartToEnd){
                isUpdated = true
                true
            }else{
                false
            }

        }
    )

    LaunchedEffect(key1 = isUpdated) {
        if(isUpdated){
            delay(animationDuration.toLong())
        }
    }

    if(isUpdated){
       appViewModel.updateShow("add article")
    }
    AnimatedVisibility(visible = !isUpdated,
        exit = shrinkVertically (
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {

        SwipeToDismissBox(
            state = state,
            backgroundContent = { UpdateArticle(swipeDismissState = state)},
            enableDismissFromEndToStart = true,
            content = {content(item)}
        )
    }
}


