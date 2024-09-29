package com.aymen.store.model.entity.realm

import com.aymen.metastore.model.entity.realm.User
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.Enum.Type
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Invetation : RealmObject {

    @PrimaryKey
    var id : Long? = null

    var client: User? = null

    var companySender: Company? = null

    var companyReciver: Company? = null

    var salary: Double? = 0.0

    var jobtitle: String? = ""

    var department: String? = ""

    var totdayvacation: Long? = 0

    var statusvacation: Boolean? = false

    var status: String = Status.INWAITING.toString()

    var type: String = Type.USER_SEND_CLIENT_COMPANY.toString()


    var createdDate : String = ""

    var lastModifiedDate : String = ""

}