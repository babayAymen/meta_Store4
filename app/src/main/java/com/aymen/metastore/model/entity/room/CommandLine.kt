package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "command_line",
    foreignKeys = [
        ForeignKey(entity = Article::class, parentColumns = ["id"], childColumns = ["articleId"]),
        ForeignKey(entity = Invoice::class, parentColumns = ["id"], childColumns = ["invoiceId"]),
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


    )
