package com.aymen.store.ui.screen.admin

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.paging.compose.itemKey
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import com.aymen.metastore.model.entity.converterRealmToApi.mapCategoryToRoomCategory
import com.aymen.store.dependencyInjection.BASE_URL
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.CategoryViewModel
import com.aymen.store.ui.component.ButtonSubmit
import com.aymen.store.ui.component.CategoryCardForAdmin
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel

@Composable
fun CategoryScreen() {
    val appViewModel : AppViewModel = hiltViewModel()
    val categoryViewModel : CategoryViewModel = hiltViewModel()
    val sharedViewModel : SharedViewModel = hiltViewModel()
//    val company by sharedViewModel.company.collectAsStateWithLifecycle()
    val user by sharedViewModel.user.collectAsStateWithLifecycle()

//    LaunchedEffect(company.id!!) {
//      //  categoryViewModel.getAllCategoryByCompany(sharedViewModel.company.value.id!!)
//    }
    val categories = categoryViewModel.categories.collectAsLazyPagingItems()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ){
        LazyColumn (
            modifier = Modifier.fillMaxWidth()
        ){
            item {
                ButtonSubmit(labelValue = "Add Category", color = Color.Green, enabled = true) {
                    appViewModel.updateShow("add category")
                }
            }
items(
    count = categories.itemCount,
    key = { it },
) { index ->
    val category = categories[index]
    if (category != null) {
        SwipeToDeleteContainer(
            item = category,
            onDelete = { /* handle delete action */ },
            appViewModel = appViewModel
        ) { categoryItem ->
            CategoryCardForAdmin(
                category = mapCategoryToRoomCategory(categoryItem),
                image = "${BASE_URL}werehouse/image/${if (category.image?.isNotEmpty() == true) category.image else ""}/category/${user.id}"
            )
        }
    } else {
        // Placeholder or loading state
       // PlaceholderItem()
    }
}
//                items(
//                    items = categories,
//                    key = { it.id?:0 }
//                ) {
//                            SwipeToDeleteContainer(
//                                it,
//                                onDelete = {
//
//                                },
//                                appViewModel = appViewModel
//                            ) {category ->
//                                CategoryCardForAdmin(
//                                    category = category,
//                                    image = "${BASE_URL}werehouse/image/"+if(category.image == ""){category.image}else{""}+"/category/${user.id}"
//                                )
//                            }

//                        }
        }
    }
}

