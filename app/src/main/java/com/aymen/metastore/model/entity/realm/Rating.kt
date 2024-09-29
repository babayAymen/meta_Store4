package com.aymen.metastore.model.entity.realm

import com.aymen.metastore.model.Enum.RateType
import com.aymen.store.model.entity.realm.Company
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey


class Rating : RealmObject {

    @PrimaryKey
    var id : Long? = null

    var raterUser: User? = null


    var rateeUser: User? = null


    var raterCompany: Company? = null


    var rateeCompany: Company? = null

    var comment: String? = null

    var photo: String? = null

    var type : String? = RateType.COMPANY_RATE_USER.toString()

    var rateValue : Int? = 0

    var createdDate : String = ""

    var lastModifiedDate : String = ""
}