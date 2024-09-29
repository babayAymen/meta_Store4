package com.aymen.store.model.entity.realm

import com.aymen.metastore.model.entity.realm.User
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class ClientProviderRelation : RealmObject {

    @PrimaryKey
    var id : Long ? = null

    var person: User? = null

    var client: Company? = null

    var provider: Company? = null

    var mvt: Double? = null

    var credit: Double? = null

    var advance: Double? = null

    var createdDate : String = ""

    var lastModifiedDate : String = ""
}