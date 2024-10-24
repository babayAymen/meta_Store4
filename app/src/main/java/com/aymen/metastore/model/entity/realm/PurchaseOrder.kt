package com.aymen.store.model.entity.realm

import com.aymen.metastore.model.entity.realm.User
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
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
    var purchaseorderlines: RealmList<PurchaseOrderLine> = realmListOf()
    override fun toString(): String {
        return "com.aymen.metastore.model.entity.room.Company(id=$id, company=$company, client=$client, person=$person, orderNumber=$orderNumber)"
    }
}