package com.aymen.metastore.model.entity.model

data class Like (

    var id : Long? = null,

    var users: List<User> = emptyList(),

    var companies: List<Company> = emptyList(),

    var article: Article? = null,

    var createdDate : String = "",

    var lastModifiedDate : String = "",
)