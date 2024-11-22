package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
@Entity(tableName = "bank_transfer",
    foreignKeys = [ForeignKey(entity = Invoice::class, parentColumns = ["id"], childColumns = ["invoiceId"])],
    indices = [Index("invoiceId")]
)
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
