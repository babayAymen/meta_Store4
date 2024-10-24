package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.Inventory

@Dao
interface InventoryDao {

    @Upsert
    suspend fun insertInventory(inventory: Inventory)

    @Query("SELECT * FROM inventory")
    suspend fun getAllInventories(): List<Inventory>


}