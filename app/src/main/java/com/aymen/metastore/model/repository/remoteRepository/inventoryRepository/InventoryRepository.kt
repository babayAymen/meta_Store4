package com.aymen.store.model.repository.remoteRepository.inventoryRepository

import com.aymen.store.model.entity.dto.InventoryDto
import retrofit2.Response

interface InventoryRepository {

    suspend fun getInventory(companyId : Long): Response<List<InventoryDto>>
 }