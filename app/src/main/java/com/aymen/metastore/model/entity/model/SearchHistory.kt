package com.aymen.metastore.model.entity.model

import com.aymen.store.model.Enum.SearchCategory

data class SearchHistory (
    val id : Long? = null,
    val  company : Company? = null,
    val article : ArticleCompany? = null,
    val user : User? = null,
    val userRelation : ClientProviderRelation? = null,
    val clientRelation : ClientProviderRelation? = null,
    val searchCategory : SearchCategory? = SearchCategory.OTHER,
    val createdDate : String? = "",
    val lastModifiedDate : String? = "",
)