package com.aymen.store.model.entity.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.Date

class Bill : RealmObject {

     @PrimaryKey
     var id : Long? = null

     var number: String = ""

     var amount: Double? = null

     var agency: String = ""

     var bankAccount: String = ""

     var delay: String? = null

     var invoice: Invoice? = null

     var createdDate : String = ""

     var lastModifiedDate : String = ""
}