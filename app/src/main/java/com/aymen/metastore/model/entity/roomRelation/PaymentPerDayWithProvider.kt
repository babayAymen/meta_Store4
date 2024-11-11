package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.Company
import com.aymen.metastore.model.entity.room.PaymentForProviderPerDay

data class PaymentPerDayWithProvider(
    @Embedded val paymentForProviderPerDay: PaymentForProviderPerDay,

    @Relation(
        parentColumn = "providerId",
        entityColumn = "id"
    )
    val provider: Company
)
