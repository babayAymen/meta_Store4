package com.aymen.store.model.entity.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Serializable

class Provider : RealmObject {

    @PrimaryKey
    var id : Long? = null

    var provider : Company? = Company()
    var client : Company? = null
    var mvt : Double = 0.0
    var credit : Double = 0.0
    var isDeleted : Boolean = false
    var advance : Double = 0.0

    var createdDate : String = ""

    var lastModifiedDate : String = ""
}