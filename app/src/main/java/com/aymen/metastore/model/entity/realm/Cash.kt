package com.aymen.store.model.entity.realm

import com.aymen.store.model.Enum.Status
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey

class Cash : RealmObject {

     @PrimaryKey
     var id : Long? = null

     var transactionId: String = ""

     var amount: Double? = null

     var status: String? = Status.INWAITING.toString()

     var invoice: Invoice? = null

     var createdDate : String = ""

     var lastModifiedDate : String = ""
}