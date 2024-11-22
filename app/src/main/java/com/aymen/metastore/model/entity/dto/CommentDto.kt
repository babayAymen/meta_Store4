package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.Comment

data class CommentDto(

    val id : Long? = null,
    val content: String = "",
    val user: UserDto? = null,
    val company: CompanyDto? = null,
    val article: ArticleDto? = null,
    val createdDate : String = "",
    val lastModifiedDate : String = "",
){
    fun toComment() : Comment {

        return Comment(
            id = id,
            content = content,
            userId = user?.id,
            companyId = company?.id,
            articleId = article?.id,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}
