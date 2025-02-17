package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "provider_remote_keys")
data class ProviderRemoteKeysEntity(
    @PrimaryKey (autoGenerate = false) val id : Long,
    val prevPage : Int?,
    val nextPage : Int?,
    val isSearch : Boolean
)
