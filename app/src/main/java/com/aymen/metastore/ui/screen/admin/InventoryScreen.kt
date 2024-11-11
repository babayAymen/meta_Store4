package com.aymen.store.ui.screen.admin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aymen.store.dependencyInjection.BASE_URL
import com.aymen.store.model.repository.ViewModel.InventoryViewModel
import com.aymen.store.ui.component.InventoryCard

@Composable
fun InventoryScreen() {
    val inventoryViewModel : InventoryViewModel = hiltViewModel()
    val inventories by inventoryViewModel.inventories.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        inventoryViewModel.getInventory()
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        LazyColumn {
            items(inventories){

                    InventoryCard(it,
                        image = "${BASE_URL}werehouse/image/${it.article.article.image}/article/${it.article.company.category!!.ordinal}")
            }
        }
    }
}