package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.model.PaymentForProviderPerDay

@Entity(tableName = "payment_for_provider_per_day",
    foreignKeys = [
        ForeignKey(entity = Company::class, parentColumns = ["companyId"], childColumns = ["receiverId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["receiverId"])]
)
data class PaymentForProviderPerDay(

    @PrimaryKey
    val id: Long? = null,
    val receiverId: Long? = null,
    val isPayed: Boolean? = null,
    val amount: Double? = null,
    val createdDate : String? = null,
    val lastModifiedDate : String? = null,
    val rest : Double? = null
){
    fun toPaymentForProviderPerDay(receiver : com.aymen.metastore.model.entity.model.Company): PaymentForProviderPerDay{
        return PaymentForProviderPerDay(
            id = id,
            receiver = receiver,
            isPayed = isPayed,
            amount = amount,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate,
            rest = rest
        )

    }
}
