package com.aymen.store.model.entity.realm

import com.aymen.metastore.model.Enum.InvoiceDetailsType
import com.aymen.metastore.model.entity.realm.User
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import java.time.LocalDateTime
import java.util.Date


class Invoice :RealmObject {

    @PrimaryKey
    var id : Long ? = null

    var code: Long = 0

    var tot_tva_invoice: Double = 0.0

    var prix_invoice_tot: Double = 0.0

    var prix_article_tot: Double = 0.0

    var discount: Double = 0.0

    var status: String = ""

    var createdDate : String = ""

    var lastModifiedDate : String = ""

    var lastModifiedBy: String = ""

    var person: User? = null

    var client: Company? = null

    var provider: Company? = null

    var paid: String = ""

    var type : String? = InvoiceDetailsType.COMMAND_LINE.toString()

    var rest: Double = 0.0

    var createdBy: String = ""
}