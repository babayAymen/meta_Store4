package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.metastore.model.Enum.InvoiceDetailsType
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status

@Entity(tableName = "invoice",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["personId"]),
        ForeignKey(
            entity = Company::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(entity = Company::class, parentColumns = ["id"], childColumns = ["providerId"]),
    ])
data class Invoice(
    
    @PrimaryKey val id : Long? = null,
    
    val code : Long? = null,

    val tot_tva_invoice : Double = 0.0,

    val prix_invoice_tot : Double =0.0,

    val prix_article_tot : Double = 0.0,

    val discount : Double = 0.0,

    val status : Status? = Status.INWAITING,

    val paid : PaymentStatus? = PaymentStatus.NOT_PAID,

    val type : InvoiceDetailsType? = InvoiceDetailsType.COMMAND_LINE,

    val rest : Double = 0.0,

    val personId : Long? = null,

    val clientId : Long? = null,

    val providerId : Long? = null,

    val createdDate : String? = null,

    val lastModifiedDate : String? = null,

    val lastModifiedBy: String? = null,

    val createdBy: String? = null
)
