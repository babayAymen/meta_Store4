package com.aymen.metastore.model.entity.realm

import com.aymen.store.model.entity.realm.Company
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.math.BigDecimal


class PaymentForProviderPerDay : RealmObject {

     @PrimaryKey
     var id: Long? = null

     var provider: Company? = null

     var payed: Boolean? = null

     var amount: Double? = null

     var createdDate : String = ""

     var lastModifiedDate : String = ""

}