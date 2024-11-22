package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.CommandLine

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
){
    fun toCommandLine() : CommandLine {

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
