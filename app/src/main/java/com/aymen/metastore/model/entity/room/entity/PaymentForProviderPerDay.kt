package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.model.PaymentForProviderPerDay

@Entity(tableName = "payment_for_provider_per_day",
    foreignKeys = [
        ForeignKey(entity = Company::class, parentColumns = ["companyId"], childColumns = ["providerId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE)
    ])
data class PaymentForProviderPerDay(

    @PrimaryKey
    val id: Long? = null,
    val providerId: Long? = null,
    val payed: Boolean? = null,
    val amount: Double? = null,
    val createdDate : String? = "",
    val lastModifiedDate : String? = ""
){
    fun toPaymentForProviderPerDay(provider : com.aymen.metastore.model.entity.model.Company): PaymentForProviderPerDay{
        return PaymentForProviderPerDay(
            id = id,
            provider = provider,
            payed = payed,
            amount = amount,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )

    }
}
