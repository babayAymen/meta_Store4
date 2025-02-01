package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.entity.dto.SubArticleDto
import com.aymen.metastore.model.entity.room.entity.SubArticle

data class SubArticleModel (

    var id : Long ? = null,
    var childArticle : ArticleCompany? = null,
    var parentArticle : ArticleCompany? = null,
    var quantity : Double = 0.0,
    var createdDate : String = "",
    var lastModifiedDate : String = ""
){
    fun toSubArticleEntity() : SubArticle{
        return SubArticle(
            id = id,
            parentArticleId = parentArticle?.id,
            childArticleId = childArticle?.id,
            quantity = quantity,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
    fun toSubArticleDto() : SubArticleDto{
        return SubArticleDto(
            id = id,
            parentArticle = parentArticle?.toArticleCompanyDto(),
            childArticle = childArticle?.toArticleCompanyDto(),
            quantity = quantity,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}