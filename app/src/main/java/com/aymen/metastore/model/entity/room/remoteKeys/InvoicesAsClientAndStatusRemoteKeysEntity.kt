package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "invoice_as_client_and_status_remote_keys")
data class InvoicesAsClientAndStatusRemoteKeysEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val nextPage: Int?,
    val prevPage: Int?
)
