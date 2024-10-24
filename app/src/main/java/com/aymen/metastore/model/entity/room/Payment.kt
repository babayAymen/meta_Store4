package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.store.model.Enum.PaymentMode
import com.aymen.store.model.Enum.Status
import java.util.Date
@Entity(tableName = "payment",
    foreignKeys = [
        ForeignKey(entity = Invoice::class, parentColumns = ["id"], childColumns = ["invoiceId"])
    ])
data class Payment(

    @PrimaryKey
    val id : Long? = null,

    val amount : Double,

    val delay : Date,

    val agency : String,

    val bankAccount : String,

    val number : String,

    val transactionId : String,

    val status : Status? = Status.INWAITING,

    val type : PaymentMode? = PaymentMode.CASH,

    val invoiceId : Long? = null,
)
