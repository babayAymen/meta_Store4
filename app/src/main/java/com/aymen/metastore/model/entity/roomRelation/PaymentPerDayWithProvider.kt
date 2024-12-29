package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.PaymentForProviderPerDay

data class PaymentPerDayWithProvider(
    @Embedded val paymentForProviderPerDay: PaymentForProviderPerDay,

    @Relation(
        parentColumn = "receiverId",
        entityColumn = "companyId",
        entity = Company::class
    )
    val provider: CompanyWithUser? = null
){
    fun toPaymentPerDayWithProvider(): com.aymen.metastore.model.entity.model.PaymentForProviderPerDay{
        return paymentForProviderPerDay.toPaymentForProviderPerDay(
            provider?.toCompany()!!
        )
    }
}
