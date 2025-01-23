package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchase_order_remote_key")
data class PurchaseOrderRemoteKeys(
    @PrimaryKey val id  : Long?,
    val prevPage : Int?,
    val nextPage : Int?
)
