package com.aymen.metastore.ui.screen.admin

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.aymen.metastore.R
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.CategoryViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.CategoryCardForAdmin
import com.aymen.metastore.util.BASE_URL

@Composable
fun CategoryScreen() {
    val appViewModel: AppViewModel = hiltViewModel()
    val categoryViewModel: CategoryViewModel = hiltViewModel()
    val categories = categoryViewModel.categories.collectAsLazyPagingItems()

    LaunchedEffect(key1 = Unit) {
        if(categories.itemCount == 0){
            categoryViewModel.getAllCategories()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        if (categories.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            val categoryScreen = stringResource(id = R.string.category_screen)

             LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    ButtonSubmit(labelValue = stringResource(id = R.string.add_category), color = Color.Green, enabled = true) {
                        appViewModel.updateShow(categoryScreen)
                    }
                }
                items(
                    count = categories.itemCount,
                    key = categories.itemKey{ it.id!! },
                    contentType = categories.itemContentType { "category" }
                ) { index ->
                    val category = categories[index]
                    if (category != null) {
                        SwipeToDeleteContainer(
                            item = category,
                            onDelete = {
                                categoryViewModel.deleteCategory()
                            },
                            onUpdate = {item ->
                                categoryViewModel.update = true
                                categoryViewModel.assignCategoryForUpdate(item)
                                appViewModel.updateShow(categoryScreen)
                            }
                        ) { categoryItem ->
                            CategoryCardForAdmin(
                                category = categoryItem,
                                image = "${BASE_URL}werehouse/image/${if (category.image?.isNotEmpty() == true) category.image else ""}/category/${category.company?.user?.id}"
                            )
                        }
                    } else {
                        CircularProgressIndicator()
                    }
                }

            }

        }
    }
}

