package com.aymen.store.model.repository.remoteRepository.inventoryRepository

import com.aymen.store.model.entity.realm.Inventory
import retrofit2.Response

interface InventoryRepository {

    suspend fun getInventory(companyId : Long): Response<List<Inventory>>
}