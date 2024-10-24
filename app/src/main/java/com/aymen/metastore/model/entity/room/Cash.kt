package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import com.aymen.store.model.Enum.Status
import androidx.room.PrimaryKey
@Entity(tableName = "cash")
data class Cash(
    @PrimaryKey
    val id : Long? = null,

    val transactionId: String = "",

    val amount: Double? = null,

    val status: String? = Status.INWAITING.toString(),

    val invoiceId: Long? = null,

    val createdDate : String = "",

    val lastModifiedDate : String = "",
)
