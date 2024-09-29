package com.aymen.store.model.entity.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.Date

class Check : RealmObject{

    @PrimaryKey
    var id :Long? = null

    var number: String? = null

    var amount: Double? = null

    var agency: String? = null

    var delay: String? = null

    var bankAccount: String? = null

    var invoice: Invoice? = null

    var createdDate : String = ""

    var lastModifiedDate : String = ""
}