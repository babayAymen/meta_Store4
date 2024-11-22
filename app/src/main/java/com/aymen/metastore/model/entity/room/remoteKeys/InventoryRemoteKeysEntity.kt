package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "inventory_remote_keys_entity")
data class InventoryRemoteKeysEntity(
    @PrimaryKey val id : Long,
    val prevPage : Int?,
    val nextPage : Int?
)
