package com.aymen.metastore.model.entity.realm

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey


class User : RealmObject {
    @PrimaryKey
    var id : Long? = null

    var username : String = ""

    var phone: String? = null

    var address: String? = null

    var email: String? = null

    var longitude : Double? = 0.0

    var latitude : Double? = 0.0

//    var roles: RealmList<String> = realmListOf()

    var balance: Double? = 0.0

    var image : String? = null

    var rate: Double? = 0.0

    var rater: Int? = 0

    var createdDate : String = ""

    var lastModifiedDate : String = ""

}