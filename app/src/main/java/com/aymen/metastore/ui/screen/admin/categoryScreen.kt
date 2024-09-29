package com.aymen.store.ui.screen.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.store.dependencyInjection.BASE_URL
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.CategoryViewModel
import com.aymen.store.ui.component.ButtonSubmit
import com.aymen.store.ui.component.CategoryCardForAdmin
import com.aymen.metastore.R

@Composable
fun CategoryScreen() {
    val appViewModel : AppViewModel = viewModel()
    val categoryViewModel : CategoryViewModel = viewModel()
    LaunchedEffect(key1 = true) {
        categoryViewModel.getAllCategoryByCompany()
    }
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column {
                        categoryViewModel.categories.forEach() {
                            SwipeToDeleteContainer(
                                it,
                                onDelete = {

                                },
                                appViewModel = appViewModel
                            ) {category ->
                                CategoryCardForAdmin(
                                    category = category,
                                    image = "${BASE_URL}werehouse/image/"+if(category.image == ""){category.image}else{""}+"/category/${category.company?.user?.username}"
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}

