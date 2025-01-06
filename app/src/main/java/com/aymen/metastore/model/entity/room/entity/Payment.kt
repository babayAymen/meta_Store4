package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.model.Payment
import com.aymen.store.model.Enum.PaymentMode
import com.aymen.store.model.Enum.Status

@Entity(tableName = "payment",
    foreignKeys = [
        ForeignKey(entity = Invoice::class, parentColumns = ["id"], childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE)
    ])
data class Payment(

    @PrimaryKey
    val id : Long? = null,
    val amount : Double? = null,
    val delay : String? = null,
    val agency : String? = null,
    val bankAccount : String? = null,
    val number : String? = null,
    val transactionId : String? = null,
    val status : Status? = Status.INWAITING,
    val type : PaymentMode? = PaymentMode.CASH,
    val invoiceId : Long? = null,
    val lastModifiedDate : String? = null
){
    fun toPayment(invoice : com.aymen.metastore.model.entity.model.Invoice): Payment{
        return Payment(
            id = id,
            amount = amount,
            delay = delay,
            agency = agency,
            bankAccount = bankAccount,
            number = number,
            transactionId = transactionId,
            status = status,
            type = type,
            invoice = invoice,
            lastModifiedDate = lastModifiedDate
        )
    }
}
