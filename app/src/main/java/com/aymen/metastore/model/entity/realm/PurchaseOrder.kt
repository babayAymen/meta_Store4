package com.aymen.store.model.entity.realm

import com.aymen.metastore.model.entity.realm.User
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.Date

class PurchaseOrder : RealmObject {

    @PrimaryKey var id : Long? = null

    var company: Company? = null
    var client: Company? = null
    var person: User? = null
    var createdDate : String? = null
    var orderNumber: Long? = null
}