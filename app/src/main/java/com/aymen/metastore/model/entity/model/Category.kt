package com.aymen.metastore.model.entity.model

data class Category  (

    var id : Long? = null,

    var libelle : String? = null,

    var code : String ?= null,

    val image : String? = null,

    val company : Company? = null,

    var createdDate : String = "",

    var lastModifiedDate : String = "",
//    var subCategories : RealmList<com.aymen.metastore.model.entity.room.entity.SubCategory> = realmListOf()
    )