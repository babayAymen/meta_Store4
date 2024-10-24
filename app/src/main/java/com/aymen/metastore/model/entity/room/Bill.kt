package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "bill")
data class Bill(
    @PrimaryKey
    val id : Long? = null,

    val number: String = "",

    val amount: Double? = null,

    val agency: String = "",

    val bankAccount: String = "",

    val delay: String? = null,

    val invoiceId: Long? = null,

    val createdDate : String = "",

    val lastModifiedDate : String = "",
)
