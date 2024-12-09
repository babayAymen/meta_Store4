package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "command_line_by_invoice_remote_keys_entity")
data class CommandLineByInvoiceRemoteKeysEntity(
    @PrimaryKey val id : Long?,
    val prevPage : Int?,
    val nextPage : Int?
)
