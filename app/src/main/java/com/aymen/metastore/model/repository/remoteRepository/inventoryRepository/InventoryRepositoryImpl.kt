package com.aymen.store.model.repository.remoteRepository.inventoryRepository

import com.aymen.store.model.repository.globalRepository.ServiceApi
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val api : ServiceApi
)
    :InventoryRepository{
    override suspend fun getInventory(companyId : Long) = api.getInventory(companyId = companyId)
    override suspend fun getInventoryy(companyId : Long) = api.getInventoryy(companyId = companyId)
}