package com.aymen.store.model.entity.api

import com.aymen.metastore.model.entity.api.ArticleCompanyDto

data class CommandLineDto(

    val id : Long? = null,

    var quantity : Double = 0.0,

    var totTva : Double? = 0.0,

    var prixArticleTot : Double = 0.0,

    var discount : Double? = 0.0,

    var invoice : InvoiceDto? = null,

    var article : ArticleCompanyDto? = null,

    var createdDate : String = "",

    var lastModifiedDate : String = "",


)
