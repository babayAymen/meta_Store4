package com.aymen.metastore.model.entity.realm

import com.aymen.store.model.entity.realm.PurchaseOrderLine
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class PaymentForProviders : RealmObject {

    @PrimaryKey
    var id : Long ? = null

    var purchaseOrderLine :PurchaseOrderLine? = null

    var giveenespece : Double? = 0.0

    var status : Boolean? = false

    var createdDate : String = ""

    var lastModifiedDate : String = ""
}