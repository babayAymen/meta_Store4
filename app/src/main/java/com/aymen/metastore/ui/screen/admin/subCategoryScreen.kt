package com.aymen.metastore.ui.screen.admin

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.model.repository.ViewModel.AppViewModel
import com.aymen.metastore.model.repository.ViewModel.SubCategoryViewModel
import com.aymen.metastore.ui.component.ButtonSubmit
import com.aymen.metastore.ui.component.SubCategoryCardForAdmin
import com.aymen.metastore.util.BASE_URL

@Composable
fun SubCategoryScreen() {
    val appViewModel: AppViewModel = hiltViewModel()
    val subCategoryViewModel: SubCategoryViewModel = hiltViewModel()
    val subcategories = subCategoryViewModel.subCategories.collectAsLazyPagingItems()
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
            items(
                count = subcategories.itemCount,
                key = subcategories.itemKey{ it.id!! },
                ) { index ->
                val sub = subcategories[index]
                Log.e("subcategory","subcategory : $sub")
                if (sub != null) {
                    SwipeToDeleteContainer(
                        sub,
                        onDelete = {
                            subCategoryViewModel.deleteSubCategories()
                        },
                        onUpdate = {item ->
                            subCategoryViewModel.update = true
                            subCategoryViewModel.assignSubCategoryForUpdate(item)
                            appViewModel.updateShow("add subCategory")

                        }
                    ) { subCategory ->
                        SubCategoryCardForAdmin(
                            subCategory = subCategory,
                            image = "${BASE_URL}werehouse/image/" + (subCategory.image
                                ?: "") + "/subcategory/${subCategory.company?.user?.id}",
                            subCategory.category!!
                        )
                    }
                }
            }
        }
    }
}

