package com.aymen.store.model.entity.realm

import com.aymen.metastore.model.entity.realm.User
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Message : RealmObject {
    @PrimaryKey
    var id : Long? = null

    var createdDate : String = ""

    var lastModifiedDate : String = ""

    var createdBy : Long? = null

    var content: String? = ""

    var conversation : Conversation? = null
}