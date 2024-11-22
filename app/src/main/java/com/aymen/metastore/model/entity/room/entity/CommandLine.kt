package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "command_line",
    foreignKeys = [
        ForeignKey(entity = ArticleCompany::class, parentColumns = ["id"], childColumns = ["articleId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(entity = Invoice::class, parentColumns = ["id"], childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
    ])
data class CommandLine(

    @PrimaryKey val id : Long? = null,
    val quantity : Double = 0.0,
    val totTva : Double? = 0.0,
    val prixArticleTot : Double = 0.0,
    val discount : Double? = 0.0,
    val invoiceId : Long? = null,
    val articleId : Long? = null,
    val createdDate : String = "",
    val lastModifiedDate : String = "",
    ){
    fun toCommandLine(invoice : com.aymen.metastore.model.entity.model.Invoice,
                      article : com.aymen.metastore.model.entity.model.ArticleCompany) : com.aymen.metastore.model.entity.model.CommandLine{
        return com.aymen.metastore.model.entity.model.CommandLine(
            id = id,
            quantity = quantity,
            totTva = totTva,
            prixArticleTot = prixArticleTot,
            discount = discount,
            invoice = invoice,
            article = article,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}
