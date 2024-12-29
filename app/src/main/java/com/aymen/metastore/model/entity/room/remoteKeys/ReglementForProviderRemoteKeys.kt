package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reglement_forProvider_remote_keys")
data class ReglementForProviderRemoteKeys(
    @PrimaryKey val id : Long? = null,

    val prevPage : Int? = null,

    val nextPage : Int? = null
)
