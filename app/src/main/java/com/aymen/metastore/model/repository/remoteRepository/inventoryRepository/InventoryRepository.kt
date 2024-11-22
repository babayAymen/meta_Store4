package com.aymen.store.model.repository.remoteRepository.inventoryRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.InventoryDto
import com.aymen.metastore.model.entity.roomRelation.InventoryWithArticle
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface InventoryRepository {

     fun getInventory(companyId : Long): Flow<PagingData<InventoryWithArticle>>
 }