package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.entity.room.entity.CommandLine

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
){
    fun toCommandLineEntity() : CommandLine{
        return CommandLine(
            id = id,
            quantity = quantity,
            totTva = totTva,
            prixArticleTot = prixArticleTot,
            discount = discount,
            invoiceId = invoice?.id,
            articleId = article?.id,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}