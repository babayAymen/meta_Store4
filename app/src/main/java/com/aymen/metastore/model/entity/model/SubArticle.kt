package com.aymen.metastore.model.entity.model

data class SubArticle (

    var id : Long ? = null,
    var parentArticle : ArticleCompany? = null,
    var childArticle : ArticleCompany? = null,
    var quantty : Double = 0.0,
    var createdDate : String = "",
    var lastModifiedDate : String = ""
)