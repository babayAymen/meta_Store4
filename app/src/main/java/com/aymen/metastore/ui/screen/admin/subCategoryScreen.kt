package com.aymen.store.ui.screen.admin

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import coil.util.Logger
import com.aymen.store.dependencyInjection.BASE_URL
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.SubCategoryViewModel
import com.aymen.store.ui.component.ButtonSubmit
import com.aymen.store.ui.component.SubCategoryCardForAdmin
import com.aymen.metastore.R
import com.aymen.metastore.model.repository.ViewModel.SharedViewModel

@Composable
fun SubCategoryScreen() {
    val appViewModel: AppViewModel = hiltViewModel()
    val subCategoryViewModel: SubCategoryViewModel = hiltViewModel()
    val sharedViewModel: SharedViewModel = hiltViewModel()
    LaunchedEffect(key1 = true) {
        subCategoryViewModel.getAllSubCategories(sharedViewModel.company.value.id!!)
    }
    val subcategories by subCategoryViewModel.subCategories.collectAsStateWithLifecycle()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                ButtonSubmit(labelValue = "Add Sub com.aymen.metastore.model.entity.room.Category", color = Color.Green, enabled = true) {
                    appViewModel.updateShow("add subCategory")
                }
            }
            items(subcategories) {
                SwipeToDeleteContainer(
                    it,
                    onDelete = {

                    },
                    appViewModel = appViewModel
                ) { subCateghory ->
                    SubCategoryCardForAdmin(
                        subCategory = subCateghory,
                        image = "${BASE_URL}werehouse/image/" + if (subCateghory.image != null) {
                            subCateghory.image
                        } else {
                            ""
                        } + "/subcategory/${subCateghory.category?.company?.user?.username}"
                    )
                }
            }
        }
    }
}

