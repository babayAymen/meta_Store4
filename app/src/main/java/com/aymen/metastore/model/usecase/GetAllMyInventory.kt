package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.InventoryWithArticle
import com.aymen.store.model.repository.remoteRepository.inventoryRepository.InventoryRepository
import kotlinx.coroutines.flow.Flow

class GetAllMyInventory(private val repository : InventoryRepository) {

    operator fun invoke(companyId : Long) : Flow<PagingData<InventoryWithArticle>>{
        return repository.getInventory(companyId = companyId)
    }
}