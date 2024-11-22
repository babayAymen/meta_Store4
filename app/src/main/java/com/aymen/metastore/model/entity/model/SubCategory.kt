package com.aymen.metastore.model.entity.model

data class SubCategory (
    var id : Long? = null,
    val libelle : String? = null,
    val code : String? = null,
    val image : String? = null,
    val category : Category? = null,
    val company : Company? = null
)