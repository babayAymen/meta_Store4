package com.aymen.metastore.model.entity.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.aymen.metastore.model.entity.dto.ArticleCompanyDto
import com.aymen.metastore.model.entity.dto.SubArticleDto
import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.SubArticleModel
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
    val quantity : Double = 0.0,
    val createdDate : String = "",
    val lastModifiedDate : String = ""
){
    fun toSubArticle(childArticle : ArticleCompany?, parentArticle : ArticleCompany?): SubArticleModel{
        return SubArticleModel(
            id = id,
            parentArticle = parentArticle,
            childArticle = childArticle,
            quantity = quantity,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
    fun toSubArticleDto(childArticle : ArticleCompanyDto? , parentArticle : ArticleCompanyDto?) : SubArticleDto{
        return SubArticleDto(
            id = id,
            parentArticle = parentArticle,
            childArticle = childArticle,
            quantity = quantity,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}
