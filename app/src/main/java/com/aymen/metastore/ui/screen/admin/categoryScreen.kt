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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aymen.store.dependencyInjection.BASE_URL
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.CategoryViewModel
import com.aymen.store.ui.component.ButtonSubmit
import com.aymen.store.ui.component.CategoryCardForAdmin
import com.aymen.metastore.R
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel

@Composable
fun CategoryScreen() {
    val appViewModel : AppViewModel = hiltViewModel()
    val categoryViewModel : CategoryViewModel = hiltViewModel()
    val sharedViewModel : SharedViewModel = hiltViewModel()
    LaunchedEffect(key1 = true) {
        categoryViewModel.getAllCategoryByCompany(sharedViewModel.company.value.id!!)
    }
    val categories by categoryViewModel.categories.collectAsStateWithLifecycle()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ){
        LazyColumn (
            modifier = Modifier.fillMaxWidth()
        ){
            item {
                ButtonSubmit(labelValue = "Add com.aymen.metastore.model.entity.room.Category", color = Color.Green, enabled = true) {
                    appViewModel.updateShow("add category")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column {
                        categories.forEach {
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

