package com.aymen.store.model.entity.realm

import com.aymen.metastore.model.entity.realm.ArticleCompany
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.entity.api.InvoiceDto
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Serializable

class PurchaseOrderLine : RealmObject {

    @PrimaryKey
    var id : Long ? = null

    var article: ArticleCompany? = ArticleCompany()

    var quantity: Double = 0.0

    var comment: String = ""

    var status: String = Status.INWAITING.toString()

    var delivery: Boolean = false

    var purchaseorder: PurchaseOrder? = null

    var createdDate : String = ""

    var lastModifiedDate : String = ""

    var invoice : Invoice? = null
}