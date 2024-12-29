package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.model.ReglementForProviderModel
import com.aymen.metastore.model.entity.room.entity.Company
import com.aymen.metastore.model.entity.room.entity.PaymentForProviderPerDay
import com.aymen.metastore.model.entity.room.entity.ReglementForProvider

data class ReglementWithPaymentPerDay(
    @Embedded val reglementPerDay: ReglementForProvider,

    @Relation(
        parentColumn = "paymentForProviderPerDayId",
        entityColumn = "id",
        entity = PaymentForProviderPerDay::class
    )
    val paymentPerDay: PaymentPerDayWithProvider? = null,

    @Relation(
        parentColumn = "payerId",
        entityColumn = "companyId",
        entity = Company::class
    )
    val payer: CompanyWithUser? = null,
){
    fun toReglementForProviderModel() : ReglementForProviderModel{
        return reglementPerDay.toReglementForProviderModel(
            payer = payer,
            meta = null,
            paymentForProviderPerDay = paymentPerDay
        )
    }
}
