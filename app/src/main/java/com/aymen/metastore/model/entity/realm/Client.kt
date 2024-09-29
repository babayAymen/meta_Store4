package com.aymen.store.model.entity.realm

import com.aymen.metastore.model.entity.realm.User
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Client  : RealmObject {
        @PrimaryKey var id : Long? = null
        var provider : Company? = null
        var client : Company? = null
        var person : User? = null
        var mvt : Double = 0.0
        var credit : Double = 0.0
        var isDeleted : Boolean = false
        var advance : Double = 0.0

        var createdDate : String = ""

        var lastModifiedDate : String = ""
}