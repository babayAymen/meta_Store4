package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.SearchHistory
import com.aymen.store.model.Enum.SearchCategory

data class SearchHistoryDto(
    val id : Long? = null,
    val  company : CompanyDto? = null,
    val article : ArticleCompanyDto? = null,
    val user : UserDto? = null,
    val userRelation  : ClientProviderRelationDto? = null,
    val clientRelation : ClientProviderRelationDto? = null,
    val searchCategory : SearchCategory? = SearchCategory.OTHER,
    val createdDate : String? = "",
    val lastModifiedDate : String? = "",
){
    fun toSearchHistory() : SearchHistory {

        return SearchHistory (
        id = id,
        companyId = company?.id,
        articleId = article?.id,
        userId = user?.id,
        userRelationId = userRelation?.id,
        clientRelationId = clientRelation?.id,
        searchCategory = searchCategory,
        createdDate = createdDate,
        lastModifiedDate = lastModifiedDate
        )
    }
}
