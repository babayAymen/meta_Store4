package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.Invoice
import com.aymen.metastore.model.entity.room.entity.Payment

data class PaymentWithInvoice(
    @Embedded val payment : Payment,

    @Relation(
        parentColumn = "invoiceId",
        entityColumn = "id",
        entity = Invoice::class
    )
    val invoice : InvoiceWithClientPersonProvider
){
    fun toPaymentModel() : com.aymen.metastore.model.entity.model.Payment{
        return payment.toPayment(invoice.toInvoiceWithClientPersonProvider())
    }
}
