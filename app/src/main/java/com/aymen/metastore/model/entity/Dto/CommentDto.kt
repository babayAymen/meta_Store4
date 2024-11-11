package com.aymen.metastore.model.entity.Dto

import com.aymen.store.model.entity.dto.ArticleDto
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.dto.UserDto

data class CommentDto(

    val id : Long? = null,

    val content: String = "",

    val user: UserDto? = null,

    val company: CompanyDto? = null,

    val article: ArticleDto? = null,

    val createdDate : String = "",

    val lastModifiedDate : String = "",
)
