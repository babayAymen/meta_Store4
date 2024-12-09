package com.aymen.metastore.model.entity.roomRelation

import androidx.room.Embedded
import androidx.room.Relation
import com.aymen.metastore.model.entity.room.entity.ArticleCompany
import com.aymen.metastore.model.entity.room.entity.CommandLine
import com.aymen.metastore.model.entity.room.entity.Invoice

data class CommandLineWithInvoiceAndArticle(
    @Embedded val commandLine : CommandLine,
    @Relation(
        entity = Invoice::class,
        parentColumn = "invoiceId",
        entityColumn = "id"
    )
    val invoice : InvoiceWithClientPersonProvider?,

    @Relation(
        entity = ArticleCompany::class,
        parentColumn = "articleId",
        entityColumn = "id"
    )
    val article : ArticleWithArticleCompany?
){
    fun toCommandLineModel(): com.aymen.metastore.model.entity.model.CommandLine{
        return commandLine.toCommandLine(
            invoice = invoice?.toInvoiceWithClientPersonProvider()?:com.aymen.metastore.model.entity.model.Invoice(),
            article = article?.toArticleRelation()?: com.aymen.metastore.model.entity.model.ArticleCompany()
        )
    }
}
