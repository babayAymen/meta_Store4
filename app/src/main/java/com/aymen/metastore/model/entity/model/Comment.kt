package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.entity.dto.CommentDto

class Comment (

    val id : Long? = null,
    var content: String = "",
    val user: User? = null,
    val company: Company? = null,
    var article: ArticleCompany? = null,
    val createdDate : String = "",
    val lastModifiedDate : String = "",
){
    fun toCommentDto() : CommentDto{
        return CommentDto(
            id = id,
            content = content,
            user = user?.toUserDto(),
            company = company?.toCompanyDto(),
            article = article?.toArticleCompanyDto(),
            createdDate = createdDate,
        )
    }
}