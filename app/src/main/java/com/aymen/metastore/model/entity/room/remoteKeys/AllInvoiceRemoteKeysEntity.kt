package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "all_invoice_remote_keys")
data class AllInvoiceRemoteKeysEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val nextPage: Int?,
    val prevPage: Int?
)
