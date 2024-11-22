package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.room.entity.User

data class PaymentPerDayWithProvider(
    @Embedded val paymentForProviderPerDay: PaymentForProviderPerDay,

    @Relation(
        parentColumn = "providerId",
        entityColumn = "userId",
        entity = Company::class
    )
    val provider: CompanyWithUser
){
    fun toPaymentPerDayWithProvider(): com.aymen.metastore.model.entity.model.PaymentForProviderPerDay{
        return paymentForProviderPerDay.toPaymentForProviderPerDay(
            provider.toCompany()
        )
    }
}
