package com.aymen.store.model.entity.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class BankTransfer : RealmObject {

     @PrimaryKey
     var id : Long? = null

     var transactionId: String = ""

     var amount: Double? = null

     var agency: String = ""

     var invoice: Invoice? = null

     var bankAccount: String = ""

     var createdDate : String = ""

     var lastModifiedDate : String = ""
}