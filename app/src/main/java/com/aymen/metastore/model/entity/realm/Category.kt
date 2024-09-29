package com.aymen.store.model.entity.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Serializable

class Category  : RealmObject {
    @PrimaryKey
    var id : Long? = null
    var libelle : String = ""
    var code : String = ""
    var image : String? = ""
    var company : Company? = Company()

    var createdDate : String = ""

    var lastModifiedDate : String = ""
//    var subCategories : RealmList<SubCategory> = realmListOf()
}