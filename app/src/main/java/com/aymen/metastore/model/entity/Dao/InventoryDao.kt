package com.aymen.metastore.model.entity.Dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.Inventory
import com.aymen.metastore.model.entity.room.remoteKeys.InventoryRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.InventoryWithArticle

@Dao
interface InventoryDao {

    @Upsert
    suspend fun insertInventory(inventory: List<Inventory>)

    @Upsert
    suspend fun insertRemoteKeys(keys : List<InventoryRemoteKeysEntity>)

    @Query("SELECT * FROM inventory_remote_keys_entity WHERE id = :id")
    suspend fun getInventoryRemoteKey(id : Long) : InventoryRemoteKeysEntity

    @Transaction
    @Query("SELECT * FROM inventory")
     fun getAllInventories(): PagingSource<Int,InventoryWithArticle>

     @Query("DELETE FROM inventory_remote_keys_entity")
     suspend fun clearAllRemoteKeysTabels()

     @Query("DELETE FROM inventory")
     suspend fun clearAllInventoryTables()

     @Transaction
     @Query("SELECT * FROM inventory")
     suspend fun testAll() : List<InventoryWithArticle>

}