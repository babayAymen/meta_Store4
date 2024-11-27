package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.model.Payment
import com.aymen.store.model.Enum.PaymentMode
import com.aymen.store.model.Enum.Status
import java.util.Date
@Entity(tableName = "payment",
    foreignKeys = [
        ForeignKey(entity = Invoice::class, parentColumns = ["id"], childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE)
    ])
data class Payment(

    @PrimaryKey
    val id : Long? = null,
    val amount : Double,
    val delay : String,
    val agency : String,
    val bankAccount : String,
    val number : String,
    val transactionId : String,
    val status : Status? = Status.INWAITING,
    val type : PaymentMode? = PaymentMode.CASH,
    val invoiceId : Long? = null,
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
            invoice = invoice
        )
    }
}
