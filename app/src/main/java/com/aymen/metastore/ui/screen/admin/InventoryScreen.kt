package com.aymen.metastore.ui.screen.admin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.aymen.metastore.dependencyInjection.BASE_URL
import com.aymen.metastore.model.repository.ViewModel.InventoryViewModel
import com.aymen.metastore.ui.component.InventoryCard

@Composable
fun InventoryScreen() {
    val inventoryViewModel : InventoryViewModel = hiltViewModel()
    val inventories = inventoryViewModel.inventories.collectAsLazyPagingItems()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        LazyColumn {
            items(
                count = inventories.itemCount,
                key = inventories.itemKey { it.id!! }) { index ->
                val inventory = inventories[index]
                if (inventory != null) {
                    InventoryCard(
                        inventory,
                        image = "${BASE_URL}werehouse/image/${inventory.article?.article?.image}/article/${inventory.article?.company?.category!!.ordinal}"
                    )
                }
            }
        }
    }
}