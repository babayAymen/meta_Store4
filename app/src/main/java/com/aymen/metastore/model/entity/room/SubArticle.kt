package com.aymen.metastore.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import io.realm.kotlin.types.annotations.PrimaryKey
@Entity(tableName = "sub_article",
    foreignKeys = [
        ForeignKey(entity = Article::class, parentColumns = ["id"], childColumns = ["parentArticleId"]),
        ForeignKey(
            entity = Article::class,
            parentColumns = ["id"],
            childColumns = ["childArticleId"],
            onDelete = ForeignKey.SET_NULL
        ),
    ])
data class SubArticle(
    @PrimaryKey
    val id : Long ? = null,
    
    val parentArticleId : Long? = null,
    val childArticleId : Long? = null,
    val quantty : Double = 0.0,

    val createdDate : String = "",

    val lastModifiedDate : String = ""
)
