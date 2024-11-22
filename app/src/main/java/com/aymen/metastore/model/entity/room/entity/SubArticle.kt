package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.SubArticle
import io.realm.kotlin.types.annotations.PrimaryKey
@Entity(tableName = "sub_article",
    foreignKeys = [
        ForeignKey(entity = com.aymen.metastore.model.entity.room.entity.ArticleCompany::class, parentColumns = ["id"], childColumns = ["parentArticleId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE),
        ForeignKey(
            entity = com.aymen.metastore.model.entity.room.entity.ArticleCompany::class,
            parentColumns = ["id"],
            childColumns = ["childArticleId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
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
){
    fun toSubArticle(childArticle : ArticleCompany?, parentArticle : ArticleCompany?): SubArticle{
        return SubArticle(
            id = id,
            parentArticle = parentArticle,
            childArticle = childArticle,
            quantty = quantty,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}
