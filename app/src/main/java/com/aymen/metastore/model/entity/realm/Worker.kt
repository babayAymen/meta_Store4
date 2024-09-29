package com.aymen.store.model.entity.realm

import com.aymen.metastore.model.entity.realm.User
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Worker : RealmObject {

    @PrimaryKey
    var id : Long ? = null
    var name: String? = null

    var phone: String? = null

    var email: String? = null

    var address: String? = null

    var salary: Double? = null

    var jobtitle: String? = null

    var department: String? = null

    var totdayvacation: Long = 0

    var remainingday: Long = 0

    var statusvacation = false

    var user: User? = null

    var company: Company? = null

    var createdDate : String = ""

    var lastModifiedDate : String = ""
}