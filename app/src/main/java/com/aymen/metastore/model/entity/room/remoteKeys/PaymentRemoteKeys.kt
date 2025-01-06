package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "payment_remote_keys")
data class PaymentRemoteKeys(
    @PrimaryKey val id : Long? = null,

    val prevPage : Int? = null,
    val nextPage : Int? = null
)
