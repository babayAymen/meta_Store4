package com.aymen.store.model.entity.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class SubArticle : RealmObject {
    @PrimaryKey
    var id : Long ? = null
    var parentArticle : Article? = null
    var childArticle : Article? = null
    var quantty : Double = 0.0

    var createdDate : String = ""

    var lastModifiedDate : String = ""
}