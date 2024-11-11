package com.aymen.metastore.model.entity.Dto

import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.dto.UserDto

data class SearchHistoryDto(
    val id : Long? = null,

    val  company : CompanyDto? = null,

    val article : ArticleCompanyDto? = null,

    val user : UserDto? = null,

    val searchCategory : SearchCategory? = SearchCategory.OTHER,

    val createdDate : String? = "",

    val lastModifiedDate : String? = "",
)
