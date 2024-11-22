package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "client_provider_remote_keys_table")
data class ClientProviderRemoteKeysEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val nextPage: Int?,
    val previousPage: Int?
)
