package com.aymen.store.ui.screen.admin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aymen.store.dependencyInjection.BASE_URL
import com.aymen.store.model.repository.ViewModel.AppViewModel
import com.aymen.store.model.repository.ViewModel.SubCategoryViewModel
import com.aymen.store.ui.component.ButtonSubmit
import com.aymen.store.ui.component.SubCategoryCardForAdmin
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
    val user by sharedViewModel.user.collectAsStateWithLifecycle()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                ButtonSubmit(labelValue = "Add Sub Category", color = Color.Green, enabled = true) {
                    appViewModel.updateShow("add subCategory")
                }
            }
            itemsIndexed(subcategories) {index , sub ->
                SwipeToDeleteContainer(
                    sub,
                    onDelete = {

                    },
                    appViewModel = appViewModel
                ) { subCategory ->

                    subCategoryViewModel.getCategoryById(subCategory.categoryId!!)
                    val relation by subCategoryViewModel.getCategoryFlow(subCategory.categoryId).collectAsStateWithLifecycle()
                    relation?.let {
                        SubCategoryCardForAdmin(
                            subCategory = subCategory,
                            image = "${BASE_URL}werehouse/image/" + (subCategory.image
                                ?: "") + "/subcategory/${user.id}",
                             it.category
                        )
                    }
                }
            }
        }
    }
}

