package com.aymen.metastore.ui.screen.admin

import android.util.Log
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
import com.aymen.metastore.model.repository.ViewModel.InventoryViewModel
import com.aymen.metastore.ui.component.InventoryCard
import com.aymen.metastore.util.BASE_URL
import com.aymen.metastore.util.IMAGE_URL_ARTICLE

@Composable
fun InventoryScreen() {
    val inventoryViewModel : InventoryViewModel = hiltViewModel()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        val inventories = inventoryViewModel.inventories.collectAsLazyPagingItems()
        LazyColumn {
            items(
                count = inventories.itemCount,
                key = inventories.itemKey { it.id!! }) { index ->
                val inventory = inventories[index]
                if (inventory != null) {
                    InventoryCard(
                        inventory,
                        image = String.format(IMAGE_URL_ARTICLE,inventory.article?.article?.image,inventory.article?.article?.category!!.ordinal)
                    )
                }
            }
        }
    }
}