package com.aymen.store.model.entity.realm

import com.aymen.metastore.model.entity.realm.User
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Parent : RealmObject {
    @PrimaryKey
    var id : Long? = null
    var name: String = ""
    var code: String = ""
    var matfisc: String = ""
    var address: String = ""
    var phone: String = ""
    var bankaccountnumber: String = ""
    var margin: Double = 0.0
    var email: String = ""
    var indestrySector: String = ""
    var capital: String = ""
    var logo: String = ""
    var workForce: Int = 0
    var rate: Double = 0.0
    var raters: Int = 0
    var user : User? = null

    var createdDate : String = ""

    var lastModifiedDate : String = ""
}