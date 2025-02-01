package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.model.ArticleCompany
import com.aymen.metastore.model.entity.model.SubArticleModel
import com.aymen.metastore.model.entity.room.entity.SubArticle

data class SubArticleDto(
    var id : Long ? = null,
    var parentArticle : ArticleCompanyDto? = null,
    var childArticle : ArticleCompanyDto? = null,
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
    fun toSubArticleModel() : SubArticleModel{
        return SubArticleModel(
            id = id ,
            parentArticle = parentArticle?.toArticleCompanyModel(),
            childArticle = childArticle?.toArticleCompanyModel(),
            quantity = quantity,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}
