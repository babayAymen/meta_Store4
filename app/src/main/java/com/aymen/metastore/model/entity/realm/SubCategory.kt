package com.aymen.store.model.entity.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Serializable

class SubCategory : RealmObject {

    //  @PrimaryKey var _id : ObjectId = ObjectId()

    @PrimaryKey
    var id : Long? = null
    var libelle: String = ""
    var code: String = ""
    var image: String? = ""
    var category : Category? = Category()

    var createdDate : String = ""

    var lastModifiedDate : String = ""
}