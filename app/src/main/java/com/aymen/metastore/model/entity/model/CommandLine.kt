package com.aymen.metastore.model.entity.model

data class CommandLine (

    val id : Long? = null,
    var quantity : Double = 0.0,
    var totTva : Double? = 0.0,
    var prixArticleTot : Double = 0.0,
    var discount : Double? = 0.0,
    var invoice : Invoice? = null,
    var article : ArticleCompany? = null,
    var createdDate : String = "",
    var lastModifiedDate : String = "",
)