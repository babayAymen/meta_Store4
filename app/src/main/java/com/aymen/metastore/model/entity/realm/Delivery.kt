package com.aymen.store.model.entity.realm

import com.aymen.metastore.model.entity.realm.User
import com.aymen.store.model.Enum.DeliveryCategory
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey


class Delivery :RealmObject {

    @PrimaryKey
    var id : Long ? = null
    var user: User? = null

    var rate: Long? = 0

    var category: String? = DeliveryCategory.MEDIUM.toString()

    var createdDate : String = ""

    var lastModifiedDate : String = ""
}