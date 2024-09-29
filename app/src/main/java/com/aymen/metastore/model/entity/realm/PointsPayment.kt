package com.aymen.store.model.entity.realm

import com.aymen.metastore.model.entity.realm.User
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class PointsPayment : RealmObject {

    @PrimaryKey
    var id : Long? = null

     var amount: Long? = 0

     var provider: Company? = null

     var clientCompany: Company? = null

     var clientUser: User? = null

    var createdDate : String = ""

    var lastModifiedDate : String = ""
}