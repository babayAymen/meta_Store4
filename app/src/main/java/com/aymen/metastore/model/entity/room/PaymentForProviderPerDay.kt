package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "payment_for_provider_per_day",
    foreignKeys = [
        ForeignKey(entity = Company::class, parentColumns = ["id"], childColumns = ["providerId"])
    ])
data class PaymentForProviderPerDay(

    @PrimaryKey
    val id: Long? = null,

    val providerId: Long? = null,

    val payed: Boolean? = null,

    val amount: Double? = null,

    val createdDate : String? = "",

    val lastModifiedDate : String? = ""
)
