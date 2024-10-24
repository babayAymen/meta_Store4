package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "bank_transfer")
data class bankTransfer(
    @PrimaryKey
    val id : Long? = null,

    val transactionId: String = "",

    val amount: Double? = null,

    val agency: String = "",

    val invoiceId: Long? = null,

    val bankAccount: String = "",

    val createdDate : String = "",

    val lastModifiedDate : String = "",
)
