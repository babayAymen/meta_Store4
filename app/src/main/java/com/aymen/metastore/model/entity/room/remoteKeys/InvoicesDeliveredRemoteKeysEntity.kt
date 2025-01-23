package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices_delivered_remote_keys")
data class InvoicesDeliveredRemoteKeysEntity(
    @PrimaryKey val id : Long?,
    val prevPage : Int?,
    val nextPage : Int ?
)
