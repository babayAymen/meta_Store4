package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.model.ReglementForProviderModel
import com.aymen.metastore.model.entity.roomRelation.CompanyWithUser
import com.aymen.metastore.model.entity.roomRelation.PaymentPerDayWithProvider

@Entity(tableName = "reglement_for_provider",
    foreignKeys = [
        ForeignKey(entity = Company::class, parentColumns = ["companyId"], childColumns = ["payerId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
         ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["metaId"],
             onDelete = ForeignKey.CASCADE,
             onUpdate = ForeignKey.CASCADE
         ),
        ForeignKey(entity = PaymentForProviderPerDay::class, parentColumns = ["id"], childColumns = ["paymentForProviderPerDayId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
    )
data class ReglementForProvider(

    @PrimaryKey
     val id : Long? = null,

     val payerId : Long? = null,

     val amount : Double? = null,

     val isAccepted : Boolean? = false,

     val metaId : Long? = null,

     val paymentForProviderPerDayId : Long? = null,

     val createdDate : String? = null,

     val lastModifiedDate : String? = null

){
    fun toReglementForProviderModel(
        payer : CompanyWithUser? = null,
        meta : User? = null,
        paymentForProviderPerDay : PaymentPerDayWithProvider? = null
    ) : ReglementForProviderModel{
        return ReglementForProviderModel(
            id,
            payer = payer?.toCompany(),
            amount,
            isAccepted,
            meta = meta?.toUser(),
            paymentForProviderPerDay = paymentForProviderPerDay?.toPaymentPerDayWithProvider(),
            createdDate,
            lastModifiedDate
        )
    }
}
