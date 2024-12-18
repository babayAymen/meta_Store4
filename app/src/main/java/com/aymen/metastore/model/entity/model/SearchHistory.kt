package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.entity.room.entity.SearchHistory
import com.aymen.store.model.Enum.SearchCategory

data class SearchHistory (
    val id : Long? = null,
    var company : Company? = null,
    var article : ArticleCompany? = null,
    var user : User? = null,
    val userRelation : ClientProviderRelation? = null,
    val clientRelation : ClientProviderRelation? = null,
    val searchCategory : SearchCategory? = SearchCategory.OTHER,
    val createdDate : String? = "",
    val lastModifiedDate : String? = "",
){
    fun toSearchHistoryEntity() : SearchHistory{
        return SearchHistory(
            id = id,
            companyId = company?.id,
            articleId = article?.id,
            userId = user?.id,
            userRelationId = userRelation?.id,
            clientRelationId = clientRelation?.id,
            searchCategory = searchCategory,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate,
        )
    }
}