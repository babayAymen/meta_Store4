package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aymen.metastore.model.Enum.InvoiceDetailsType
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status

@Entity(tableName = "invoice",
    foreignKeys = [
        ForeignKey(entity = User::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(
            entity = Company::class,
            parentColumns = ["companyId"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(entity = Company::class,
            parentColumns = ["companyId"],
            childColumns = ["providerId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
    ],
    indices = [Index("personId"), Index("clientId"), Index("providerId")]
)
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
    val createdBy: String? = null,
    val isInvoice : Boolean?
){
    fun toInvoice(user : com.aymen.metastore.model.entity.model.User?,
                  client : com.aymen.metastore.model.entity.model.Company?,
                  provider : com.aymen.metastore.model.entity.model.Company): Invoice {
        return com.aymen.metastore.model.entity.model.Invoice(
            id = id,
            code = code,
            tot_tva_invoice = tot_tva_invoice,
            prix_invoice_tot = prix_invoice_tot,
            prix_article_tot = prix_article_tot,
            discount = discount,
            status = status,
            paid = paid,
            type = type,
            rest = rest,
            person = user,
            client = client,
            provider = provider,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate,
            lastModifiedBy = lastModifiedBy,
            createdBy = createdBy
        )

    }
}
